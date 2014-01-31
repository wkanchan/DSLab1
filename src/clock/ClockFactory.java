package clock;

import lab0.ds.LogicalClock;
import lab0.ds.VectorClock;

public class ClockFactory {

	public static ClockService useClock(ClockType model, int numberOfProcesses) {
		ClockService clock = null;
		switch (model) {
		case LOGICAL:
			clock = new LogicalClock();
			break;
		case VECTOR:
			clock = new VectorClock(numberOfProcesses);
			break;
		default:
			// throw some exception
			break;
		}
		return clock;
	}
}
