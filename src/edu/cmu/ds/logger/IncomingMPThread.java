package edu.cmu.ds.logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import lab0.ds.TimeStampedMessage;

public class IncomingMPThread extends Thread {

	private Socket incomingMPSocket;
	private ConcurrentLinkedQueue<TimeStampedMessage> messagesList;
	
	public IncomingMPThread(Socket incomingMPSocket, ConcurrentLinkedQueue<TimeStampedMessage> messagesList) {
		this.incomingMPSocket = incomingMPSocket;
		this.messagesList = messagesList;
	}
	
	@Override
	public void run() {
		System.out.println("\nMessagePasser connected!");
		ObjectInputStream in = null;
		TimeStampedMessage incomingMessage = null;
		try {
			in = new ObjectInputStream(incomingMPSocket.getInputStream());
			while (true) {
				incomingMessage = (TimeStampedMessage) in.readObject();
				System.out.println("\nMessage received! "+incomingMessage);
				if (incomingMessage == null) {
					break;
				}
				messagesList.add(incomingMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			incomingMPSocket.close();
			in.close();
		} catch (IOException e) {
		} finally {
			System.out.println("Disconnected by the message sender.");
		}
	}
}
