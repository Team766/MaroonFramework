package com.team766.simulator.elements;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.team766.simulator.PhysicalConstants;
import com.team766.simulator.interfaces.MechanicalDevice;
import com.team766.simulator.interfaces.PneumaticDevice;

public class SingleActingPneumaticCylinder implements PneumaticDevice, MechanicalDevice {
	private static final Vector3D FORWARD = new Vector3D(1, 0, 0);

	private final double boreDiameter;
	private final double stroke;
	private final double returnSpringForce;

	private boolean isExtended = false;
	private boolean commandExtended = false;

	private PneumaticDevice.Input pneumaticState = new PneumaticDevice.Input(0);

	public SingleActingPneumaticCylinder(final double boreDiameterParam, final double strokeParam,
			final double returnSpringForceParam) {
		this.boreDiameter = boreDiameterParam;
		this.stroke = strokeParam;
		this.returnSpringForce = returnSpringForceParam;
	}

	public void setExtended(final boolean state) {
		commandExtended = state;
	}

	@Override
	public PneumaticDevice.Output step(final PneumaticDevice.Input input) {
		pneumaticState = input;
		PneumaticDevice.Output output;
		double deviceVolume = isExtended ? boreArea() * stroke : 0;
		if (isExtended && !commandExtended) {
			output = new PneumaticDevice.Output(
					-deviceVolume * (input.pressure + PhysicalConstants.ATMOSPHERIC_PRESSURE)
							/ PhysicalConstants.ATMOSPHERIC_PRESSURE,
					deviceVolume);
		} else {
			output = new PneumaticDevice.Output(0, deviceVolume);
		}
		isExtended = commandExtended;
		return output;
	}

	@Override
	public MechanicalDevice.Output step(final MechanicalDevice.Input input) {
		Vector3D direction = isExtended ? FORWARD : FORWARD.negate();
		double force = isExtended ? boreArea() * pneumaticState.pressure : returnSpringForce;
		return new MechanicalDevice.Output(direction.scalarMultiply(force));
	}

	private double boreArea() {
		return Math.PI * Math.pow(boreDiameter / 2., 2);
	}
}
