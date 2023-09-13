package com.team766.controllers;
	/*
	* This is a class to make an offset point for the PID controller to be able to reset encoders.
	* You input the absoluteEncoderValue that equals the motorEncoderValue.
	* @author Max Spier - 9/12/23
	*/
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
