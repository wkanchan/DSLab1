package lab0.ds;

public class VectorClock extends ClockService {

	public VectorClock() {
		super(ClockType.VECTOR);
	}

	/*remove this */
	@Override
	protected void incrementTimeStamp(TimeStampedMessage message) {
		// TODO Auto-generated method stub
		
	}

}
