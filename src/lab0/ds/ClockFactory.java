package lab0.ds;

public class ClockFactory {
	public static ClockService useClock(ClockType model) {
		ClockService clock = null;
		switch (model) {
		case LOGICAL:
			clock = new LogicalClock();
			break;
		case VECTOR:
			clock = new VectorClock();
			break;
		default:
			// throw some exception
			break;
		}
		return clock;
	}
}
