package clock;

import lab0.ds.TimeStamp;



public class ClockFactory {
	private static ClockType model;

	public static ClockService useClock(ClockType model, int numberOfProcesses, int local_id) {
		ClockService clock = null;
		ClockFactory.model = model; 

		switch (model) {
		case LOGICAL:
			clock = new LogicalClock();
			break;
		case VECTOR:
			clock = new VectorClock(numberOfProcesses, local_id);
			break;
		default:
			// throw some exception
			break;
		}
		return clock;
	}

	public static TimeStamp getTimeStamp() {
		switch (model) {
		case LOGICAL:
			return LogicalClock.timeStamp;
		case VECTOR:
			return VectorClock.timeStamp;
		default:
			break;
		}
		return null;
	}

	public static void setTimeStamp(TimeStamp timeStamp) {
		switch (model) {
		case LOGICAL:
			LogicalClock.timeStamp = timeStamp;
		case VECTOR:
			VectorClock.timeStamp = timeStamp;
		default:
			break;
		}
	
	}
}
