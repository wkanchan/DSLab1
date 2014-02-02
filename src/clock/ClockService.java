package clock;

import lab0.ds.TimeStampedMessage;


public abstract class ClockService {

	protected ClockType clockType = null;

	public ClockService(ClockType clockType) {
		this.clockType = clockType;
	}

	public ClockType getClockType() {
		return clockType;
	}

	public void setClockType(ClockType clockType) {
		this.clockType = clockType;
	}

	/**
	 * Just increment by one
	 */
	public abstract void incrementTimeStamp();
	
	/**
	 * Increment by comparing to message's timestamp
	 * @param message
	 */
	public abstract void incrementTimeStamp(TimeStampedMessage message);

}
