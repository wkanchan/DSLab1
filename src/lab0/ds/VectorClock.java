package lab0.ds;

import clock.ClockService;
import clock.ClockType;

public class VectorClock extends ClockService {
	
	public static TimeStamp timeStamp;

	public VectorClock(int numberOfProcesses) {
		super(ClockType.VECTOR);
		timeStamp = new TimeStamp(0, new Integer[numberOfProcesses]);
	}

	@Override
	protected void incrementTimeStamp(TimeStampedMessage message) {
		synchronized (this) {
			  timeStamp.setVec_timeStamp((Math.max(timeStamp.getVec_timeStamp(), (Integer)message.getVecTimeStamp()) + 1);
		}
	}

	@Override
	public TimeStamp getTimeStamp() {
		return timeStamp;
	}

}
