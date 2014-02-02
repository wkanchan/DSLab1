package clock;

import lab0.ds.TimeStamp;
import lab0.ds.TimeStampedMessage;

public class VectorClock extends ClockService {
	
	static TimeStamp timeStamp;
	private int local_id;

	public VectorClock(int numberOfProcesses, int local_id) {
		super(ClockType.VECTOR);
		timeStamp = new TimeStamp(0, new int[numberOfProcesses]);
		this.local_id = local_id;
	}
	
	@Override
	public void incrementTimeStamp() {
		synchronized (timeStamp) {
			timeStamp.setVec_timeStamp(local_id, timeStamp.getVec_timeStamp(local_id) + 1);
		}
	}

	@Override
	public void incrementTimeStamp(TimeStampedMessage message) {
		synchronized (timeStamp) {
			int[] messageVectorTimeStamp = message.getTimeStamp().getVec_timeStamp();
			for (int i=0; i<messageVectorTimeStamp.length; i++) {
				if (i == local_id) {
					  timeStamp.setVec_timeStamp(local_id, 
							  Math.max(
									  timeStamp.getVec_timeStamp(local_id), 
									  messageVectorTimeStamp[local_id]) + 1
							  );
				} else {
					timeStamp.setVec_timeStamp(i, messageVectorTimeStamp[i]);
				}
			}
		}
	}

}
