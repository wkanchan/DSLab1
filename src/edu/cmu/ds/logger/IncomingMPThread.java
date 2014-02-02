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
		System.out.print("\nMessagePasser connected!\n" + MPLogger.COMMAND_PROMPT);
		ObjectInputStream in = null;
		TimeStampedMessage incomingMessage = null;
		try {
			in = new ObjectInputStream(incomingMPSocket.getInputStream());
			while (true) {
				incomingMessage = (TimeStampedMessage) in.readObject();
				if (incomingMessage == null) {
					break;
				}
				messagesList.add(incomingMessage);
				System.out.print("\nLogged! " + incomingMessage + "\n" + MPLogger.COMMAND_PROMPT);
			}
		} catch (Exception e) {
//			e.printStackTrace();
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
