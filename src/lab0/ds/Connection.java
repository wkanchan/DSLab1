package lab0.ds;

import java.io.*;
import java.net.*;

import javax.swing.JTextArea;

public class Connection {
	Socket socket;
	ObjectInputStream inputStream;
	ObjectOutputStream outputStream;
	
	JTextArea textArea;
	
	public Connection(Socket socket, JTextArea textArea) {
		this.textArea = textArea;
		this.socket = socket;
		
		// set up output stream
		try {
			outputStream = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			textArea.append("Create output stream error!\n");
			return ;
		}
		
		// set up input stream
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
        	textArea.append("Create socket input stream fail!\n");
            return ;
        }
	}

	public ObjectInputStream getInputStream() {
		return inputStream;
	}

	public ObjectOutputStream getOutputStream() {
		return outputStream;
	}
	
	public void close() {
		try {
			outputStream.close();
		} catch (IOException e) {
		}
	}
}
