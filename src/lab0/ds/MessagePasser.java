package lab0.ds;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.*;
import java.util.concurrent.*;

import javax.swing.JTextArea;

public class MessagePasser {
	private String configurationFileName;
	private String localName;
	private int sequenceNumber;

	private ConfigurationFileReader configurationFileReader;
	private RuleChecker ruleChecker;

	private ConcurrentLinkedQueue<TimeStampedMessage> sendBuffer;
	private ConcurrentLinkedQueue<TimeStampedMessage> receiveBuffer;
	private ConcurrentLinkedDeque<TimeStampedMessage> delayedMessageBuffer; // store received messages that are delayed

	private ServerSocket serverSocket;
	private int localPortNumber;
	private ConcurrentHashMap<String, Connection> connectionPool;

	private JTextArea textArea;

	private ClockService clockService;
	public MessagePasser(JTextArea textArea) {
		this.textArea = textArea;
	}

	public boolean run(String configurationFileName, String localName, String clock) {
		this.configurationFileName = configurationFileName;
		this.localName = localName;
		this.sequenceNumber = 1;
		if(clock.contains("logical")) {
			clockService = ClockFactory.useClock(ClockType.LOGICAL);
		}
		else if(clock.contains("vector")) {
			clockService = ClockFactory.useClock(ClockType.VECTOR);
		}
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
		Runnable receiveConnectionJob = new ReceiveConnectionJob(serverSocket, connectionPool, 
				receiveBuffer, configurationFileReader, textArea);
		Thread receiveConnectionThread = new Thread(receiveConnectionJob);
		receiveConnectionThread.start();

		return true;
	}

	public void send(Message message) {

		// check if file is modified
		File file = new File(configurationFileName);
		
		@SuppressWarnings("unused")
		TimerTask task = new FileModified(file) {
			protected void onChange( File file ) {
				/* read from dropbox folder */
				configurationFileReader.parseFile(configurationFileName);
				ruleChecker.setSendRules(configurationFileReader.getSendRules());
				ruleChecker.setReceiveRules(configurationFileReader.getReceiveRules());
				textArea.append( "The file " + file.getName() +" is changed\n" );
			}
		};

		// Check if connection is already existed, if not, firstly create the connection
		Connection connection = null;
		if (!connectionPool.containsKey(message.getDestination())) {
			connection = createConnection(message.getDestination(), configurationFileReader.getProcesses());
			if (connection == null) {
				textArea.append("Cannot create connection, give up sending message!\n");
				return ;
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
		timeStampedMessage.setTimeStamp(ClockService.getTimeStamp());
		
		// Check send rule
		ArrayList<TimeStampedMessage> toSendMessages = ruleChecker.checkSendRule(timeStampedMessage, sendBuffer);
		if (toSendMessages.isEmpty()) { // if no message need to be sent now
			return ;
		}

		// Send message
		for (TimeStampedMessage toSendMessage: toSendMessages) {
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
					return ;
				}
				connection.getOutputStream().writeObject(toSendMessage);
				textArea.append("Send a message to " + toSendMessage.getDestination() + "\n");
			} catch (IOException e) {
			}
		}

		// Send out all delayed message
		if (!sendBuffer.isEmpty()) {
			for (TimeStampedMessage toSendMessage: sendBuffer) {
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
			protected void onChange( File file ) {
				//code the action on a change
				/* read from dropbox folder */
				configurationFileReader.parseFile(configurationFileName);
				ruleChecker.setSendRules(configurationFileReader.getSendRules());
				ruleChecker.setReceiveRules(configurationFileReader.getReceiveRules());
				textArea.append( "The file " + file.getName() +" is changed\n" );
			}
		};

		/* Check delayedMessageBuffer, if not empty, directly get one message and return it */
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
			entry.getValue().close();;
		} 
	}

	private Connection createConnection(String destination, ArrayList<Process> processes) {
		Connection connection = null;

		// Search the process
		Process requiredProcess = null;
		for (Process process: processes) {
			if (process.getName().equals(destination)) {
				requiredProcess = process;
				break ;
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
			Runnable receiveMessageJob = new ReceiveMessageJob(connection, receiveBuffer, 
					textArea, connectionPool);
			Thread receiveMessageThread = new Thread(receiveMessageJob);
			receiveMessageThread.start();	
		} catch (UnknownHostException e) {
			textArea.append("Unknown host when connecting!\n");
		} catch (IOException e) {
			textArea.append("IOException when connecting!\n");
		}

		return connection;
	}

	private void sendDelayedMessage(TimeStampedMessage message) {
		String destination = message.getDestination();
		Connection connection = null;
		if (!connectionPool.containsKey(message.getDestination())) {
			connection = createConnection(message.getDestination(), configurationFileReader.getProcesses());
			if (connection == null) {
				textArea.append("Cannot create connection, give up sending message!\n");
				return ;
			}
		} else {
			connection = connectionPool.get(destination);
		}

		if (connection == null) {
			textArea.append("Connection doesn't exsit\n");
			return ;
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
				return ;
			}
			connection.getOutputStream().writeObject(message);
		} catch (IOException e) {
			textArea.append("Send message fail!\n");
		}
	}
}
