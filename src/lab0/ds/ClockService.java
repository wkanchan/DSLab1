package lab0.ds;

import java.util.HashMap;

public abstract class ClockService {

	protected ClockType clockType = null;
	
	/**
	 * There is only single clock in the system. So, this is a static variable that stores current time stamp.
	 * Protected: We want this to be modifiable only to our logical & vector clocks.
	 */
	protected static TimeStamp timeStamp = new TimeStamp(0, new HashMap<String, Integer>());
	
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
}
