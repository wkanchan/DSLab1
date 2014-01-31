package edu.cmu.ds.logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedQueue;

import lab0.ds.ClockType;
import lab0.ds.ConfigurationFileReader;
import lab0.ds.TimeStamp;
import lab0.ds.TimeStampedMessage;

public class MPLogger {
	
	public final static String COMMAND_PROMPT = "MPLogger# ";
	
	private ConcurrentLinkedQueue<TimeStampedMessage> messagesList;

	private ConfigurationFileReader configurationFileReader;
	private int localPortNumber;
	private ServerSocket serverSocket;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: <Config file name>");
		} else {
			new MPLogger(args[0]);
		}
	}
	
	public MPLogger(String configurationFileName) {
		// Parse configuration file
		configurationFileReader = new ConfigurationFileReader(null);
		configurationFileReader.parseFile(configurationFileName);
		
		// Initiate messages buffer
		messagesList = new ConcurrentLinkedQueue<>();
		
		// Setup server socket
		localPortNumber = configurationFileReader.getLoggerInfo().getPort();
		System.out.println("Local port number: " + localPortNumber);
		if (localPortNumber == -1) {
			System.out.println("Invalid local port number!");
			return;
		}
		try {
			serverSocket = new ServerSocket(localPortNumber);
			System.out.println("Set up server socket succeeded!");
		} catch (Exception e) {
			System.out.println("Set up server socket failed!");
			return;
		}
		
		/* Run thread to receive connection */
		(new ReceiveMPConnectionThread(serverSocket, messagesList)).start();
		
		// Prompt for user command
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		printCommandPrompt();
		String inputCommand;
		try {
			while ((inputCommand = in.readLine()) != null) {
				if ("pl".equals(inputCommand) || "pv".equals(inputCommand)) {
					// -- Print --
					final ClockType CLOCK_TYPE;
					if ("pl".equals(inputCommand))
						CLOCK_TYPE = ClockType.LOGICAL;
					else
						CLOCK_TYPE = ClockType.VECTOR;
					// Sort the messages list
					ArrayList<TimeStampedMessage> sortedMessagesList = new ArrayList<>();
					try {
						for (TimeStampedMessage m: (TimeStampedMessage[])messagesList.toArray()) {
							sortedMessagesList.add(m);
						}
						Collections.sort(sortedMessagesList, new Comparator<TimeStampedMessage>() {
	
							@Override
							public int compare(TimeStampedMessage o1, TimeStampedMessage o2) {
								if (CLOCK_TYPE == ClockType.LOGICAL) {
									return o1.getTimeStamp().log_compareTo(o2.getTimeStamp());
								} else if (CLOCK_TYPE == ClockType.VECTOR) {
									return o1.getTimeStamp().vec_compareTo(o2.getTimeStamp());
								}
								return 0;
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
					// Print the sorted list
					for (TimeStampedMessage message: sortedMessagesList) {
						System.out.println(message);
					}
				} else if ("q".equals(inputCommand)) {
					// -- Quit --
					in.close();
					System.exit(0);
					break;
				} else {
					System.out.println("Unrecognized command");
				}
				printCommandPrompt();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void printCommandPrompt() {
		System.out.print("\n-------------\n"+COMMAND_PROMPT);
	}
	

}
