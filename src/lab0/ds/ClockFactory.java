package lab0.ds;

public class ClockFactory {

	private static LogicalClock logicalClock;
	private static VectorClock vectorClock;

	public static ClockService useClock(ClockType model, int numberOfProcesses) {
		if (logicalClock == null) {
			logicalClock = new LogicalClock();
		}
		if (vectorClock == null) {
			vectorClock = new VectorClock(numberOfProcesses);
		}
		switch (model) {
		case LOGICAL:
			return logicalClock;
		case VECTOR:
			return vectorClock;
		default:
			// TODO: throw some exception
			return null;
		}
	}
}
