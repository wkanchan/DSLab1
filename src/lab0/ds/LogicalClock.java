package lab0.ds;

public class LogicalClock extends ClockService {

	public LogicalClock() {
		super(ClockType.LOGICAL);
	}

	void incrementTimeStamp(TimeStampedMessage message) {
		synchronized (this) {
			timeStamp.setLog_timeStamp(Math.max(timeStamp.getLog_timeStamp(), (Integer) message.getLogTimeStamp()) + 1);
		}
	}
}
