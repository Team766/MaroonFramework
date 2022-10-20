package com.team766.hal.simulator;

import com.team766.hal.GyroReader;
import com.team766.simulator.ProgramInterface;

public class Gyro implements GyroReader{

	public Gyro() {
		reset();
	}

	@Override
	public void calibrate() {
		reset();
	}

	@Override
	public void reset() {
		ProgramInterface.gyro.angle = 0;
	}

	@Override
	public double getAngle() {
		return ProgramInterface.gyro.angle;
	}

	@Override
	public double getRate() {
		return ProgramInterface.gyro.rate;
	}

	@Override
	public double getPitch() {
		return ProgramInterface.gyro.pitch;
	}

	@Override
	public double getRoll() {
		return ProgramInterface.gyro.roll;
	}

}
