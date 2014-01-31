package lab0.ds;

public class VectorClock extends ClockService {

	public static TimeStamp timeStamp;
	
	public VectorClock() {
		super(ClockType.VECTOR);
	}

	@Override
	protected void incrementTimeStamp(TimeStampedMessage message) {
		synchronized (this) {
			  timeStamp.setVec_timeStamp((Math.max(timeStamp.getVec_timeStamp(), (Integer)message.getLogTimeStamp()) + 1);
		}
	}

}
