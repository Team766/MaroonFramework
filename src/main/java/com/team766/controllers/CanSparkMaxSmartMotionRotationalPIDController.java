import java.*;

public class CanSparkMaxSmartMotionRotationalPIDController extends ExtendableCanSparkMaxSmartMotionPIDController {
	
	private final double degreesToEncoderUnitsRatio;
	private final double antiGravK;

	public CanSparkMaxSmartMotionRotationalPIDController(final String configName, final double absEncoderOffset, final double absEncoderOffsetForZeroEncoderUnits, final OffsetPoint first, final OffsetPoint second) {
		super(configName, absEncoderOffset, absEncoderOffsetForZeroEncoderUnits, first, second);
	}

	public void updateDegreesToEncoderUnitsRatio(double ratio){
		degreesToEncoderUnitsRatio = ratio;
	}

	public void setAntigravConstant(double k) {
		antiGravK = k;
	}

	public void setNewSetpoint(double setPoint){
		updateSetpointFromSubclass(setPoint);
	}

	private double euToDegrees(final double eu) {
		return eu * degreesToEncoderUnitsRatio;
	}

	public void runPIDs(){
		updateAntigrav(antiGravK * Math.sin(euToDegrees(mc.getSensorPosition())));
		run();
	}


}
