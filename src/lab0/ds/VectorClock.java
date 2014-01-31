package lab0.ds;

public class VectorClock extends ClockService {
	
	public static TimeStamp timeStamp;

<<<<<<< HEAD
	public static TimeStamp timeStamp;
	
	public VectorClock() {
=======
	public VectorClock(int numberOfProcesses) {
>>>>>>> ee5a10a3b5a12d9d8e8cfcf51ccdb6a6de27fa79
		super(ClockType.VECTOR);
		timeStamp = new TimeStamp(0, new int[numberOfProcesses]);
	}
<<<<<<< HEAD

=======

	/*remove this */
>>>>>>> ee5a10a3b5a12d9d8e8cfcf51ccdb6a6de27fa79
	@Override
	protected void incrementTimeStamp(TimeStampedMessage message) {
		synchronized (this) {
			  timeStamp.setVec_timeStamp((Math.max(timeStamp.getVec_timeStamp(), (Integer)message.getLogTimeStamp()) + 1);
		}
	}

	@Override
	public TimeStamp getTimeStamp() {
		return timeStamp;
	}

}
