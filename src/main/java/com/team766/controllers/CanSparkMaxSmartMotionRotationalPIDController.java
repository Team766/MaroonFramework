import java.*;

public class CanSparkMaxSmartMotionRotationalPIDController extends ExtendableCanSparkMaxSmartMotionPIDController {
	
	private final double degreesToEncoderUnitsRatio;
	private final double antiGravK;
 
	public CanSparkMaxSmartMotionRotationalPIDController(final String configName, final double absEncoderOffset, final double absEncoderOffsetForZeroEncoderUnits, final OffsetPoint first, final OffsetPoint second, double degToEncoderUnitsRatio, double antigravityCoefficent) {
		antiGravK = antigravityCoefficent;
		degreesToEncoderUnitsRatio = degToEncoderUnitsRatio;
		super(configName, absEncoderOffset, absEncoderOffsetForZeroEncoderUnits, first, second);
	}
	
	//Setpoint is in hall encoder units.
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
