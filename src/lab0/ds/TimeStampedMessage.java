package lab0.ds;

import java.io.Serializable;

import clock.ClockService;
import clock.ClockType;

public class TimeStampedMessage extends Message implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ClockService clockService;
	private TimeStamp timeStamp;
	private ClockType clockType;

	public TimeStampedMessage(Message message) {
		super(message);
	}

	public TimeStampedMessage(String source, String destination, int sequenceNumber, boolean duplicate, String kind,
			Object data) {
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

	public TimeStamp getTimeStamp() {
		return timeStamp;
	}
	
	public void setTimeStamp(TimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	public ClockType getClockType() {
		return clockType;
	}

	public void setClockType(ClockType clockType) {
		this.clockType = clockType;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("TimeStampedMessage[");
		buf.append("LOGICAL <");
		buf.append(timeStamp.getLog_timeStamp());
		buf.append(">, VECTOR <");
//		for (int t : timeStamp.getVec_timeStamp()) {
//			buf.append(t + ",");
//		}
		buf.append(">, SequenceNumber=" + super.sequenceNumber + ", ");
		buf.append("Source=" + super.source + ", ");
		buf.append("Destination=" + super.destination + ", ");
		buf.append("Kind=" + super.kind + "]");
		return buf.toString();
	}
}
