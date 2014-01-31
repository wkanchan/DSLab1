package lab0.ds;

import java.util.HashMap;

public abstract class ClockService {

	protected ClockType clockType = null;
	
	protected static TimeStamp timeStamp;
	
	public static TimeStamp getCurrentTimeStamp() {
		return timeStamp;
	}
	
	public ClockService(ClockType clockType) {
		this.clockType = clockType;
	}
	
	public ClockType getClockType() {
		return clockType;
	}
	
	public void setClockType(ClockType clockType) {
		this.clockType = clockType;
	}
	
	protected abstract void incrementTimeStamp(TimeStampedMessage message);

	public static TimeStamp getTimeStamp() {
		return LogicalClock.timeStamp;
	}

	public static void setTimeStamp(TimeStamp timeStamp) {
		LogicalClock.timeStamp = timeStamp;
	}
}
