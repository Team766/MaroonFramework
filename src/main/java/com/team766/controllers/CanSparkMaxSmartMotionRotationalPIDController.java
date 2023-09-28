package com.team766.controllers;

import java.util.*;

public class CanSparkMaxSmartMotionRotationalPIDController extends ExtendableCanSparkMaxSmartMotionPIDController {
	
	private final double degreesToEncoderUnitsRatio;
	private final double antiGravK;
 
	public CanSparkMaxSmartMotionRotationalPIDController(final String configName, final double absEncoderOffset, final double absEncoderOffsetForZeroEncoderUnits, final OffsetPoint first, final OffsetPoint second, double degToEncoderUnitsRatio, double antigravityCoefficent, NeutralMode defaultNeutralMode) {
		super(configName, absEncoderOffset, absEncoderOffsetForZeroEncoderUnits, first, second, defaultNeutralMode);
		antiGravK = antigravityCoefficent;
		degreesToEncoderUnitsRatio = degToEncoderUnitsRatio;
	}
	

	private double euToDegrees(final double eu) {
		return eu * degreesToEncoderUnitsRatio;
	}

	public void runPIDs(){
		updateAntigrav(antiGravK * Math.sin(euToDegrees(getHallSensorPosition())));
		run();
	}


}
