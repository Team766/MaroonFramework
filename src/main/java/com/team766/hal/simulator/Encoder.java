package com.team766.hal.simulator;

import com.team766.hal.EncoderReader;
import com.team766.simulator.ProgramInterface;

public class Encoder implements EncoderReader {

	private final int channel;
	private double distancePerPulse = 1.0;

	public Encoder(final int a, final int b) {
		this.channel = a;
	}

	@Override
	public int get() {
		return (int) ProgramInterface.encoderChannels[channel].distance;
	}

	@Override
	public void reset() {
		set(0);
	}

	@Override
	public boolean getStopped() {
		return getRate() == 0;
	}

	@Override
	public boolean getDirection() {
		return getRate() > 0;
	}

	@Override
	public double getDistance() {
		return get() * distancePerPulse;
	}

	@Override
	public double getRate() {
		return ProgramInterface.encoderChannels[channel].rate * distancePerPulse;
	}

	@Override
	public void setDistancePerPulse(final double distancePerPulse_) {
		this.distancePerPulse = distancePerPulse_;
	}

	public void set(final int tick) {
		ProgramInterface.encoderChannels[channel].distance = tick;
	}

}
