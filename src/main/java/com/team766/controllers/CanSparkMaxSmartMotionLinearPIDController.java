public class CanSparkMaxSmartMotionLinearPIDController extends ExtendableCanSparkMaxSmartMotionPIDController {
	
	private double antigravPower;


	public CanSparkMaxSmartMotionLinearPIDController(final String configName, final double absEncoderOffset, final double absEncoderOffsetForZeroEncoderUnits, final OffsetPoint first, final OffsetPoint second) {
		super(configName, absEncoderOffset, absEncoderOffsetForZeroEncoderUnits, first, second);
	}

	

	public void runPIDs(){
		updateAntigrav(antigravPower);
		run();
	}

}
