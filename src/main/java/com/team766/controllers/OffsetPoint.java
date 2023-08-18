package com.team766.controllers;

public class OffsetPoint {
	
	private double absoluteValue, motorValue;

	public OffsetPoint(final double absoluteEncoderValue, final double motorEncoderValue) {
		absoluteValue = absoluteEncoderValue;
		motorValue = motorEncoderValue;
	}

	public double getAbsoluteValue() {
		return absoluteValue;
	}

	public double getMotorEncoderValue() {
		return motorValue;
	}
}
