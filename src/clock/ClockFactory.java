package clock;



public class ClockFactory {

	public static ClockService useClock(ClockType model, int numberOfProcesses, int local_id) {
		ClockService clock = null;
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
}
