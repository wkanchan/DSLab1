package lab0.ds;

public abstract class ClockService {

	private ClockType clockType = null;
	
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
}
