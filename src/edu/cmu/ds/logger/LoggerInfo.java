package edu.cmu.ds.logger;

public class LoggerInfo {

	private String ipAddress;
	private int port;
	
	public LoggerInfo(String ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
	}
	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
}
