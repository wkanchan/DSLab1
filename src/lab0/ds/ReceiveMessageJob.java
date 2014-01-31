package lab0.ds;
import java.io.*;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.*;

import javax.swing.JTextArea;


public class ReceiveMessageJob implements Runnable {
    
    private Connection connection;
    private ClockService clockService;
    private ConcurrentLinkedQueue<TimeStampedMessage> receiveBuffer;
    private ConcurrentHashMap<String, Connection> connectionPool;
    
    private JTextArea textArea;
    
    public ReceiveMessageJob (Connection connection, ConcurrentLinkedQueue<TimeStampedMessage> receiveBuffer2,
    	JTextArea textArea, ConcurrentHashMap<String, Connection> connectionPool, ClockService clockService) {
        this.connection = connection;        
        this.receiveBuffer = receiveBuffer2;
        this.textArea = textArea;
        this.connectionPool = connectionPool;
        this.clockService = clockService;
    }
    
    @Override
    public void run() {
    	textArea.append("A connection thread run!\n");    
        
    	// Receive message
        while (true) {
            try {
            	if (connection.getInputStream() == null) {
            		textArea.append("Connection's input stream doesn't exist\n");
            		return ;
            	}
                TimeStampedMessage message = (TimeStampedMessage)connection.getInputStream().readObject();
                
                /* Add timestamp to the message */
        		clockService.incrementTimeStamp(message);
        		message.setTimeStamp(ClockService.getTimeStamp());
        		
                if (message != null) {
                    receiveBuffer.add(message);
                }
            } catch (ClassNotFoundException e) {
            	textArea.append("Cast object fail!\n");
            } catch (IOException e) {
            	textArea.append("Close this socket. ");
                break;
            }
        }
        
        Iterator<Entry<String, Connection>> iter = connectionPool.entrySet().iterator(); 
		while (iter.hasNext()) { 
			Entry<String, Connection> entry = iter.next();
			if (entry.getValue() == connection) {
				connectionPool.remove(entry.getKey());
				entry.getValue().close();
				break;
			}
		}
        
        textArea.append("Thread exit...\n");
    }
}
