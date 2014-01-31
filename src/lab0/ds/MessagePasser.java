package lab0.ds;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JTextArea;

import clock.ClockFactory;
import clock.ClockService;
import clock.ClockType;

import edu.cmu.ds.logger.LoggerInfo;

public class MessagePasser {
	private String configurationFileName;
	private String localName;
	private int sequenceNumber;

	private ConfigurationFileReader configurationFileReader;
	private RuleChecker ruleChecker;

	private ConcurrentLinkedQueue<TimeStampedMessage> sendBuffer;
	private ConcurrentLinkedQueue<TimeStampedMessage> receiveBuffer;
	private ConcurrentLinkedDeque<TimeStampedMessage> delayedMessageBuffer; // store
																			// received
																			// messages
																			// that
																			// are
																			// delayed

	private ServerSocket serverSocket;
	private int localPortNumber;
	private ConcurrentHashMap<String, Connection> connectionPool;

	private JTextArea textArea;
	private Socket loggerSocket;
	private ObjectOutputStream loggerOut;

	private ClockService clockService;

	public MessagePasser(JTextArea textArea) {
		this.textArea = textArea;
	}

	public boolean run(String configurationFileName, String localName, String clock) {
		this.configurationFileName = configurationFileName;
		this.localName = localName;
		this.sequenceNumber = 1;
		connectionPool = new ConcurrentHashMap<String, Connection>();

		// Initiate buffer
		sendBuffer = new ConcurrentLinkedQueue<TimeStampedMessage>();
		receiveBuffer = new ConcurrentLinkedQueue<TimeStampedMessage>();
		delayedMessageBuffer = new ConcurrentLinkedDeque<TimeStampedMessage>();

		// Parse configuration file
		configurationFileReader = new ConfigurationFileReader(textArea);
		configurationFileReader.parseFile(this.configurationFileName);

		// Initiate ruleChecker
		ruleChecker = new RuleChecker(configurationFileReader.getSendRules(),
				configurationFileReader.getReceiveRules(), textArea);

		// Initiate a connection to logger
		try {
			LoggerInfo loggerInfo = configurationFileReader.getLoggerInfo();
			loggerSocket = new Socket(loggerInfo.getIpAddress(), loggerInfo.getPort());
			loggerOut = new ObjectOutputStream(loggerSocket.getOutputStream());
			textArea.append("Connected to logger at " + loggerInfo.getIpAddress() + ":" + loggerInfo.getPort() + "\n");
		} catch (Exception e) {
			textArea.append("Cannot connect to logger\n");
		}

		// Initiate a clock
		if (clock.contains("logical")) {
			clockService = ClockFactory.useClock(ClockType.LOGICAL, 0, 0);
		} else if (clock.contains("vector")) {
			clockService = ClockFactory.useClock(ClockType.VECTOR, configurationFileReader.getProcesses().size(), configurationFileReader.get);
		}

		// Setup server socket
		localPortNumber = configurationFileReader.getProcessPortByName(localName);
		textArea.append("Local port number: " + localPortNumber + "\n");
		if (localPortNumber == -1) {
			textArea.append("Invalid local port number!\n");
			return false;
		}
		try {
			serverSocket = new ServerSocket(localPortNumber);
			textArea.append("Set up server socket success!\n");
		} catch (Exception e) {
			textArea.append("Set up server socket fail!\n");
			return false;
		}

		/* Run thread to receive connection */
		Runnable receiveConnectionJob = new ReceiveConnectionJob(serverSocket, connectionPool, receiveBuffer,
				configurationFileReader, textArea, clockService);
		Thread receiveConnectionThread = new Thread(receiveConnectionJob);
		receiveConnectionThread.start();

		return true;
	}

	public void send(Message message) {

		// check if file is modified
		File file = new File(configurationFileName);

		@SuppressWarnings("unused")
		TimerTask task = new FileModified(file) {
			protected void onChange(File file) {
				/* read from dropbox folder */
				configurationFileReader.parseFile(configurationFileName);
				ruleChecker.setSendRules(configurationFileReader.getSendRules());
				ruleChecker.setReceiveRules(configurationFileReader.getReceiveRules());
				textArea.append("The file " + file.getName() + " is changed\n");
			}
		};

		// Check if connection is already existed, if not, firstly create the
		// connection
		Connection connection = null;
		if (!connectionPool.containsKey(message.getDestination())) {
			connection = createConnection(message.getDestination(), configurationFileReader.getProcesses());
			if (connection == null) {
				textArea.append("Cannot create connection, give up sending message!\n");
				return;
			}
		} else {
			connection = connectionPool.get(message.getDestination());
		}

		TimeStampedMessage timeStampedMessage = new TimeStampedMessage(message);
		// Set message's header
		timeStampedMessage.setSequenceNumber(sequenceNumber++);
		timeStampedMessage.setSource(localName);
		timeStampedMessage.setDuplicate(false);

		/* Add timestamp to the message */
		clockService.incrementTimeStamp(timeStampedMessage);
		timeStampedMessage.setTimeStamp(clockService.getTimeStamp());

		// Check send rule
		ArrayList<TimeStampedMessage> toSendMessages = ruleChecker.checkSendRule(timeStampedMessage, sendBuffer);
		if (toSendMessages.isEmpty()) { // if no message need to be sent now
			return;
		}

		// Send message
		for (TimeStampedMessage toSendMessage : toSendMessages) {
			try {
				if (connection.getOutputStream() == null) {
					textArea.append("Connection's output stream doesn't exist\n");

					// remove this connection from connection pool
					Iterator<Entry<String, Connection>> iter = connectionPool.entrySet().iterator();
					while (iter.hasNext()) {
						Entry<String, Connection> entry = iter.next();
						if (entry.getValue() == connection) {
							connectionPool.remove(entry.getKey());
							entry.getValue().close();
							break;
						}
					}
					return;
				}
				connection.getOutputStream().writeObject(toSendMessage);
				textArea.append("Send a message to " + toSendMessage.getDestination() + "\n");

				// Log
				try {
					loggerOut.writeObject(toSendMessage);
					textArea.append("Event logged");
				} catch (Exception e) {
					e.printStackTrace();
					textArea.append("Cannot log the send event");
				}
			} catch (IOException e) {
				e.printStackTrace();
				textArea.append(e+": Couldn't send the message "+toSendMessage);
			}
		}

		// Send out all delayed message
		if (!sendBuffer.isEmpty()) {
			for (TimeStampedMessage toSendMessage : sendBuffer) {
				sendDelayedMessage(toSendMessage);
			}
			sendBuffer.clear();
			textArea.append("Send all delayed message\n");
		}
	}

	public TimeStampedMessage receive() {
		// check if file is modified
		File file = new File(configurationFileName);

		@SuppressWarnings("unused")
		TimerTask task = new FileModified(file) {
			protected void onChange(File file) {
				// code the action on a change
				/* read from dropbox folder */
				configurationFileReader.parseFile(configurationFileName);
				ruleChecker.setSendRules(configurationFileReader.getSendRules());
				ruleChecker.setReceiveRules(configurationFileReader.getReceiveRules());
				textArea.append("The file " + file.getName() + " is changed\n");
			}
		};

		/*
		 * Check delayedMessageBuffer, if not empty, directly get one message
		 * and return it
		 */
		if (!delayedMessageBuffer.isEmpty()) {
			return delayedMessageBuffer.poll();
		}

		TimeStampedMessage message = null;
		while (true) {
			/* Get a message from receive buffer */
			while (receiveBuffer.isEmpty()) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
			}
			message = receiveBuffer.poll();

			/* Check receive rule */
			TimeStampedMessage toReturnMessage = ruleChecker.checkReceiveRule(message, delayedMessageBuffer);
			if (toReturnMessage != null) {
				message = toReturnMessage;
				break;
			}
		}

		// Log
		try {
			loggerOut.writeObject(message);
			textArea.append("Event logged");
		} catch (Exception e) {
			e.printStackTrace();
			textArea.append("Cannot log the receive event");
		}

		return message;
	}

	public void stop() {
		// Close server socket
		try {
			serverSocket.close();
		} catch (IOException e) {
		}

		// Close client socket
		Iterator<Entry<String, Connection>> iter = connectionPool.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Connection> entry = iter.next();
			connectionPool.remove(entry.getKey());
			entry.getValue().close();
			;
		}
	}

	private Connection createConnection(String destination, ArrayList<Process> processes) {
		Connection connection = null;

		// Search the process
		Process requiredProcess = null;
		for (Process process : processes) {
			if (process.getName().equals(destination)) {
				requiredProcess = process;
				break;
			}
		}
		if (requiredProcess == null) {
			return null;
		}

		// Create connection
		try {
			Socket socket = new Socket(requiredProcess.getIpAddress(), requiredProcess.getPort());
			connection = new Connection(socket, textArea);
			connectionPool.put(requiredProcess.getName(), connection);
			textArea.append("Connect success!\n"); // for test

			// Create a new thread for this connection
			Runnable receiveMessageJob = new ReceiveMessageJob(connection, receiveBuffer, textArea, connectionPool,
					clockService);
			Thread receiveMessageThread = new Thread(receiveMessageJob);
			receiveMessageThread.start();
		} catch (UnknownHostException e) {
			textArea.append("Unknown host when connecting!\n");
		} catch (IOException e) {
			textArea.append("IOException when connecting!\n");
		}

		return connection;
	}

	private void sendDelayedMessage(TimeStampedMessage timeStampedMessage) {
		String destination = timeStampedMessage.getDestination();
		Connection connection = null;

		/* Add timestamp to the message */
		clockService.incrementTimeStamp(timeStampedMessage);
		timeStampedMessage.setTimeStamp(clockService.getTimeStamp());

		/* Log the event */
		try {
			loggerOut.writeObject(timeStampedMessage);
			textArea.append("Event logged");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (!connectionPool.containsKey(timeStampedMessage.getDestination())) {
			connection = createConnection(timeStampedMessage.getDestination(), configurationFileReader.getProcesses());
			if (connection == null) {
				textArea.append("Cannot create connection, give up sending message!\n");
				return;
			}
		} else {
			connection = connectionPool.get(destination);
		}

		if (connection == null) {
			textArea.append("Connection doesn't exsit\n");
			return;
		}

		try {
			if (connection.getOutputStream() == null) {
				textArea.append("Connection's output stream doesn't exist\n");

				// remove this connection from connection pool
				Iterator<Entry<String, Connection>> iter = connectionPool.entrySet().iterator();
				while (iter.hasNext()) {
					Entry<String, Connection> entry = iter.next();
					if (entry.getValue() == connection) {
						connectionPool.remove(entry.getKey());
						entry.getValue().close();
						break;
					}
				}
				return;
			}
			connection.getOutputStream().writeObject(timeStampedMessage);

		} catch (IOException e) {
			textArea.append("Send message fail!\n");
		}
	}
}
