package lab0.ds;

public abstract class ClockService {

	private ClockType clockType = null;
	public ClockType getClockType() {
		return clockType;
	}
	public void setClockType(ClockType clockType) {
		this.clockType = clockType;
	}
	public ClockService(ClockType clockType) {
		this.clockType = clockType;
	}
}
