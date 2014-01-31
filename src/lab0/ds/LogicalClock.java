package lab0.ds;

public class LogicalClock extends ClockService {

	public static TimeStamp timeStamp;
	
	public LogicalClock() {
		super(ClockType.LOGICAL);
	}

	@Override
	protected void incrementTimeStamp(TimeStampedMessage message) {
		synchronized (this) {
			  timeStamp.setLog_timeStamp(Math.max(timeStamp.getLog_timeStamp(), (Integer)message.getTimeStamp().getLog_timeStamp()) + 1);
		}		
	}

}
