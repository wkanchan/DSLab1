package lab0.ds;

public class TimeStampedMessage extends Message{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ClockService clockService;
	private TimeStamp timeStamp;
	private ClockService clockType;
	public TimeStampedMessage(Message message) {
		super(message);
	}
	
	public TimeStampedMessage(String source, String destination,
			int sequenceNumber, boolean duplicate, String kind, Object data) {
		super(source, destination, sequenceNumber, duplicate, kind, data);
	}

	public TimeStampedMessage(String destination, String kind, Object data, TimeStamp timeStamp) {
		super(destination, kind, data);
		this.setClockType(clockType);
	}
	
	public ClockService getClockService() {
		return clockService;
	}

	public void setClockService(ClockService clockService) {
		this.clockService = clockService;
	}

	public int getLogTimeStamp() {
		return timeStamp.getLog_timeStamp();
	}

	public ClockService getClockType() {
		return clockType;
	}

	public void setClockType(ClockService clockType) {
		this.clockType = clockType;
	}
	
	public TimeStamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(TimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}
}
