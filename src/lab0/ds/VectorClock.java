package lab0.ds;

public class VectorClock extends ClockService {
	
	public static TimeStamp timeStamp;

	public VectorClock(int numberOfProcesses) {
		super(ClockType.VECTOR);
		timeStamp = new TimeStamp(0, new Integer[numberOfProcesses]);
	}

	/*remove this */
	@Override
	protected void incrementTimeStamp(TimeStampedMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TimeStamp getTimeStamp() {
		return timeStamp;
	}

}
