package lab0.ds;

/**
 * The only place for system-wide logical or vector clock
 * @author kwittawat
 *
 */
public class ClockFactory {
	
	private static LogicalClock logicalClock = new LogicalClock();
	private static VectorClock vectorClock = new VectorClock();
	
	public static ClockService useClock(ClockType model) {
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
