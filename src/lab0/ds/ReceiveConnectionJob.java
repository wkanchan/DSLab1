package lab0.ds;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import javax.swing.JTextArea;


public class ReceiveConnectionJob implements Runnable {

	private ServerSocket serverSocket;
	private ConcurrentHashMap<String, Connection> connectionPool;
	private ConcurrentLinkedQueue<TimeStampedMessage> receiveBuffer;
	private ConfigurationFileReader configurationFileReader;
	
	private JTextArea textArea;

	public ReceiveConnectionJob(ServerSocket serverSocket, ConcurrentHashMap<String, Connection> connectionPool,
		ConcurrentLinkedQueue<TimeStampedMessage> receiveBuffer, ConfigurationFileReader configurationFileReader,
		JTextArea textArea) {
		this.serverSocket = serverSocket;
		this.connectionPool = connectionPool;
		this.receiveBuffer = receiveBuffer;
		this.configurationFileReader = configurationFileReader;
		this.textArea = textArea;
	}

	@Override
	public void run() {
		textArea.append("Server socket thread run!\n");
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				
				// Look up socket's name in configuration file by IP address
				String processName = configurationFileReader.getProcessNameByIp(
					socket.getInetAddress().getHostAddress());
				if (processName != null) {
					textArea.append("Receive a new connection\n");
					// Create a new connection
					Connection connection = new Connection(socket, textArea);
					connectionPool.put(processName, connection);

					// Create a new thread for this socket
					Runnable receiveMessageJob = new ReceiveMessageJob(connection, receiveBuffer,
						textArea, connectionPool);
					Thread receiveMessageThread = new Thread(receiveMessageJob);
					receiveMessageThread.start();
				}
			} catch (IOException e) {
				System.out.println("server socket exception!");
				break;
			}
		}

		System.out.println("Thread exit...");
	}
}
