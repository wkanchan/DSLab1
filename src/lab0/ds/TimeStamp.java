package lab0.ds;

import java.util.HashMap;

public class TimeStamp {
	private int log_timeStamp;
	private int[] vec_timeStamp;

	public TimeStamp(Integer log_timeStamp, int[] vec_timeStamp) {
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
	
	public int[] getVec_timeStamp() {
		return vec_timeStamp;
	}
	
	/**
	 * Compare method for logical time stamp
	 * @param o
	 * @return
	 */
	public int log_compareTo(TimeStamp o) {
		if (this.log_timeStamp < o.log_timeStamp) {
			return -1;
		} else if (this.log_timeStamp > o.log_timeStamp) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Compare method for vector time stamp
	 * @param o
	 * @return
	 */
	public int vec_compareTo(TimeStamp o) {
		if (this.vec_timeStamp.length != o.vec_timeStamp.length) {
			throw new RuntimeException("Vector timestamps have different number of entries.");
		}
		// Check condition 1: V == V' 2: V <= V' 3: V >= V'
		boolean isEqual = true, isLessThanOrEqualTo = true, isMoreThanOrEqualTo = true;
		for (int i=0; i<this.vec_timeStamp.length; i++) {
			if (this.vec_timeStamp[i] != o.vec_timeStamp[i]) {
				isEqual = false;
			}
			if (this.vec_timeStamp[i] > o.vec_timeStamp[i]) {
				isLessThanOrEqualTo = false;
			}
			if (this.vec_timeStamp[i] < o.vec_timeStamp[i]) {
				isMoreThanOrEqualTo = false;
			}
		}
		// Return result -1: V <= V' 0: V == V' or V || V' 1: V >= V'
		if (isEqual || (!isLessThanOrEqualTo && !isMoreThanOrEqualTo)) {
			return 0;
		} else if (isLessThanOrEqualTo) {
			return -1;
		} else if (isMoreThanOrEqualTo) {
			return 1;
		}
		return 0;
	}
	
}
