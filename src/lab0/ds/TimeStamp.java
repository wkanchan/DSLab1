package lab0.ds;

import java.util.HashMap;

public class TimeStamp {
	private int log_timeStamp;
	private HashMap<String, Integer> vec_timeStamp;

	public TimeStamp(Integer log_timeStamp, HashMap<String, Integer> vec_timeStamp) {
		super();
		this.log_timeStamp = log_timeStamp;
		this.vec_timeStamp = vec_timeStamp;
	}

	public int getLog_timeStamp() {
		return log_timeStamp;
	}

	public void setLog_timeStamp(Integer log_timeStamp) {
		this.log_timeStamp = log_timeStamp;
	}
	
	/**
	 * Get current timestamp for a specific process
	 * @param processName
	 * @return
	 */
	public Integer getVec_timeStamp(String processName) {
		return vec_timeStamp.get(processName);
	}
	
	/**
	 * Update vector clock by process name
	 * @param processName Process name
	 * @param value New timestamp value for that process
	 */
	public void setVec_timeStamp(String processName, Integer value) {
		vec_timeStamp.put(processName, value);
	}
	
}
