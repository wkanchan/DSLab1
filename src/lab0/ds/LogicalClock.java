package lab0.ds;

import clock.ClockService;
import clock.ClockType;

public class LogicalClock extends ClockService {

	public static TimeStamp timeStamp;
	
	public LogicalClock() {
		super(ClockType.LOGICAL);
		timeStamp = new TimeStamp(0, new int[0]);
	}

	@Override
	protected void incrementTimeStamp(TimeStampedMessage message) {
		synchronized (this) {
			if (message.getTimeStamp() == null) {
				timeStamp.setLog_timeStamp(timeStamp.getLog_timeStamp() + 1);
			} else {
			  timeStamp.setLog_timeStamp(Math.max(timeStamp.getLog_timeStamp(), (Integer)message.getTimeStamp().getLog_timeStamp()) + 1);
			}
		}		
	}

	@Override
	public TimeStamp getTimeStamp() {
		return timeStamp;
	}

}
