package lab0.ds;

import java.util.*;
import java.util.concurrent.*;

import javax.swing.JTextArea;

public class RuleChecker {
	private ArrayList<Rule> sendRules;
	private ArrayList<Rule> receiveRules;
	
	private JTextArea textArea;
	
	public RuleChecker(ArrayList<Rule> sendRules, ArrayList<Rule> receiveRules, JTextArea textArea) {
		this.sendRules = sendRules;
		this.receiveRules = receiveRules;
		this.textArea = textArea;
	}
	
	public void setSendRules(ArrayList<Rule> sendRules) {
		this.sendRules = sendRules;
	}
	
	public void setReceiveRules(ArrayList<Rule> receiveRules) {
		this.receiveRules = receiveRules;
	}
	
	public ArrayList<Message> checkSendRule(Message message, ConcurrentLinkedQueue<Message> sendBuffer) {
		// Check if matches a send rule
		Rule rule = null;
		for (Rule sendRule: sendRules) {
			// Check source, if not match, move to the next send rule
			String source = sendRule.getSource();
			if ((source != "") && (!source.equals(message.getSource()))) {
				continue;
			}
			
			// Check destination, if not match, move to the next send rule
			String destination = sendRule.getDestination();
			if ((destination != "") && (!destination.equals(message.getDestination()))) {
				continue;
			}
			
			// Check sequenceNumber, if not match, move to the next send rule
			int sequenceNumber = sendRule.getSequenceNumber();
			if ((sequenceNumber != -1 ) && (sequenceNumber != message.getSequenceNumber())) {
				continue;
			}
			
			// Check message's kind, if not match, move to the next send rule
			String kind = sendRule.getKind();
			if ((kind != "") && (!kind.equals(message.getKind()))) {
				continue;
			}
			
			rule = sendRule;
			break;
		}
		
		ArrayList<Message> toSendMessages = new ArrayList<Message>();
		if (rule != null) { // if matches a rule, do corresponding action
			String action = rule.getAction();
			if (action.equals("drop")) {
				textArea.append("a message get dropped\n"); // print it for test
			} else if (action.equals("duplicate")) {
				textArea.append("a message get duplicated\n"); // print it for test
				message.setDuplicate(false);
				toSendMessages.add(message);
				
				Message dupMessage = new Message(message);
				dupMessage.setDuplicate(true);
				toSendMessages.add(dupMessage);
			} else if (action.equals("delay")) {
				sendBuffer.add(message);
				textArea.append("a message get delayed\n"); // print it for test
			} else {
				toSendMessages.add(message);
				textArea.append("unknown send action, ignore it\n");
			}
		} else { // if doesn't match any rule, do nothing
			toSendMessages.add(message);
		}
		
		return toSendMessages;
	}
	
	public Message checkReceiveRule(Message message, ConcurrentLinkedDeque<Message> delayedMessageBuffer) {
		/* Check if matches a receive rule */
		Rule rule = null;
		for (Rule receiveRule: receiveRules) {
			// check source, if not match, move to the next send rule
			String source = receiveRule.getSource();
			if ((source != "") && (!source.equals(message.getSource()))) {
				continue;
			}
			
			// check destination, if not match, move to the next send rule
			String destination = receiveRule.getDestination();
			if ((destination != "") && (!destination.equals(message.getDestination()))) {
				continue;
			}
			
			// check sequenceNumber, if not match, move to the next send rule
			int sequenceNumber = receiveRule.getSequenceNumber();
			if ((sequenceNumber != -1 ) && (sequenceNumber != message.getSequenceNumber())) {
				continue;
			}
			
			// check message's kind, if not match, move to the next send rule
			String kind = receiveRule.getKind();
			if ((kind != "") && (!kind.equals(message.getKind()))) {
				continue;
			}
			
			rule = receiveRule;
			break;
		}
		
		Message toReceiveMessage = null;
		if (rule != null) { // if matches a rule, do corresponding action
			String action = rule.getAction();
			if (action.equals("drop")) {
				textArea.append("this message get dropped"); // print it for test
			} else if (action.equals("duplicate")) {
				textArea.append("this message get duplicated"); // print it for test
				message.setDuplicate(false);
				Message dupMessage = new Message(message);
				dupMessage.setDuplicate(true);
				delayedMessageBuffer.addFirst(message); // add the duplicate to the head of the delayed buffer
				toReceiveMessage = message;
			} else if (action.equals("delay")) {
				textArea.append("this message get delayed"); // print it for test
				delayedMessageBuffer.addLast(message);
			} else {
				textArea.append("Unknown receive action, ignore this rule");
				toReceiveMessage = message;
			}
		} else { // if doesn't match any rule, do nothing
			toReceiveMessage = message;
		}
		
		return toReceiveMessage;
	}
}
