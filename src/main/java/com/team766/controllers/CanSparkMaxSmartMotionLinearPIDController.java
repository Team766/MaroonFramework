public class CanSparkMaxSmartMotionLinearPIDController extends ExtendableCanSparkMaxSmartMotionPIDController {
	
	private final double antigravPower;


	public CanSparkMaxSmartMotionLinearPIDController(final String configName, final double absEncoderOffset, final double absEncoderOffsetForZeroEncoderUnits, final OffsetPoint first, final OffsetPoint second) {
		super(configName, absEncoderOffset, absEncoderOffsetForZeroEncoderUnits, first, second);
	}

	public void setAntigravPower(double power){
		antigravPower = power;
	}

	public void setNewSetpoint(double setPoint){
		updateSetpointFromSubclass(setPoint);
	}
	

	public void runPIDs(){
		updateAntigrav(antigravPower);
		run();
	}

}
