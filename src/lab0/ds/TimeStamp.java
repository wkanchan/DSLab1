package lab0.ds;

import java.util.ArrayList;

public class TimeStamp {
	private int log_timeStamp;
	private  int[] vec_timeStamp;

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

	public ArrayList<Integer> getVec_timeStamp() {
		return vec_timeStamp;
	}

	public void setVec_timeStamp(ArrayList<Integer> vec_timeStamp) {
		this.vec_timeStamp = vec_timeStamp;
	}
	
}
