package lab0.ds;

public class VectorClock extends ClockService {

	public VectorClock() {
		super(ClockType.VECTOR);
	}

	void incrementTimeStamp(TimeStampedMessage message){
		synchronized (this) {
		}
	}
	
	/**
	 * When there is a new process, this should be called to add the process's vector clock entry.
	 * @param processName
	 */
	public void addEntryForProcess(String processName) {
		timeStamp.setVec_timeStamp(processName, 0);
	}
}
