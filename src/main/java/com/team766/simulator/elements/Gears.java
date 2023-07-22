package com.team766.simulator.elements;

import com.team766.simulator.interfaces.MechanicalAngularDevice;

public class Gears implements MechanicalAngularDevice {
	// TODO: Add rotational inertia
	// TODO: Add losses

	// Torque ratio (output / input)
	private final double torqueRatio;

	private MechanicalAngularDevice upstream;

	public Gears(final double torqueRatioParam, final MechanicalAngularDevice upstreamParam) {
		this.torqueRatio = torqueRatioParam;
		this.upstream = upstreamParam;
	}

	@Override
	public MechanicalAngularDevice.Output step(final MechanicalAngularDevice.Input input) {
		MechanicalAngularDevice.Input upstreamInput =
			new MechanicalAngularDevice.Input(input.angularVelocity * torqueRatio);
		MechanicalAngularDevice.Output upstreamOutput = upstream.step(upstreamInput);
		return new MechanicalAngularDevice.Output(upstreamOutput.torque * torqueRatio);
	}
}
