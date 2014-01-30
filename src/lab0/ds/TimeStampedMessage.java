package lab0.ds;

public class TimeStampedMessage extends Message{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TimeStampedMessage(Message message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public TimeStampedMessage(String source, String destination,
			int sequenceNumber, boolean duplicate, String kind, Object data) {
		super(source, destination, sequenceNumber, duplicate, kind, data);
		// TODO Auto-generated constructor stub
	}

	public TimeStampedMessage(String destination, String kind, Object data) {
		super(destination, kind, data);
		// TODO Auto-generated constructor stub
	}

}
