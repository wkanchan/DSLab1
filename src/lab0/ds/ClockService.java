package lab0.ds;

import java.io.Serializable;


public abstract class ClockService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	public abstract TimeStamp getTimeStamp();
	protected abstract void incrementTimeStamp(TimeStampedMessage message);

}
