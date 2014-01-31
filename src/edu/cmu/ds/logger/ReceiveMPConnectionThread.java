package edu.cmu.ds.logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import lab0.ds.TimeStampedMessage;

public class ReceiveMPConnectionThread extends Thread {
	
	private ServerSocket serverSocket;
	private ConcurrentLinkedQueue<TimeStampedMessage> messagesList;
	
	public ReceiveMPConnectionThread(ServerSocket serverSocket, ConcurrentLinkedQueue<TimeStampedMessage> messagesList) {
		this.serverSocket = serverSocket;
		this.messagesList = messagesList;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Socket incomingMPSocket = serverSocket.accept();
				(new IncomingMPThread(incomingMPSocket, messagesList)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
