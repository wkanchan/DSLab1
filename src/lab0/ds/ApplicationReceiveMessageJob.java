package lab0.ds;

import javax.swing.*;

public class ApplicationReceiveMessageJob implements Runnable {
	private JTextArea textArea;
	private MessagePasser messagePasser;
	
	public ApplicationReceiveMessageJob(JTextArea textArea, MessagePasser messagePasser) {
		this.textArea = textArea;
		this.messagePasser = messagePasser;
	}
	
	@Override
    public void run() {
		while (true) {
			Message message = messagePasser.receive();
			if (!message.getDuplicate()) {
				printMessage(message);
			}
		}
	}
	
	private void printMessage(Message message) {
		textArea.append("\n");
		textArea.append("A new message comes\n");
    	textArea.append("Source: " + message.getSource() + "\n");
    	textArea.append("Destination: " + message.getDestination() + "\n");
    	textArea.append("SequenceNumber: " + message.getSequenceNumber() + "\n");
    	textArea.append("Duplicate: " + message.getDuplicate() + "\n");
    	textArea.append("Kind: " + message.getKind() + "\n");
    	textArea.append("Data: " + message.getData().toString() + "\n");
	}
}
