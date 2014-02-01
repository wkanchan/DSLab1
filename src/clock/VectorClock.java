package clock;

import lab0.ds.TimeStamp;
import lab0.ds.TimeStampedMessage;

public class VectorClock extends ClockService {
	
	public static TimeStamp timeStamp;
	private int local_id;

	public VectorClock(int numberOfProcesses, int local_id) {
		super(ClockType.VECTOR);
		timeStamp = new TimeStamp(0, new Integer[numberOfProcesses]);
		this.local_id = local_id;
	}

	@Override
	public void incrementTimeStamp(TimeStampedMessage message) {
		synchronized (this) {
			  timeStamp.setVec_timeStamp(local_id, (Math.max(timeStamp.getVec_timeStamp(local_id), (Integer)message.getTimeStamp().getVec_timeStamp(local_id)) + 1));
		}
	}

}
