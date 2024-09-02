package com.team766.simulator.elements;

import com.team766.simulator.interfaces.ElectricalDevice;
import com.team766.simulator.interfaces.MechanicalAngularDevice;

public class DCMotor implements ElectricalDevice, MechanicalAngularDevice {
	// TODO: Add rotor inertia
	// TODO: Add thermal effects

	public static DCMotor makeCIM() {
		return new DCMotor(12, 5330, 2.7, 2.41, 131);
	}
	public static DCMotor make775Pro() {
		return new DCMotor(12, 18730, 0.7, 0.71, 134);
	}
	public static DCMotor makeFalcon500() {
		return new DCMotor(12, 6380, 1.5, 4.69, 257);
	}
	public static DCMotor makeNeo() {
		return new DCMotor(12, 5880, 1.3, 3.36, 166);
	}
	public static DCMotor makeNeo550() {
		return new DCMotor(12, 11710, 1.1, 1.08, 111);
	}

	// Motor velocity constant in radian/second/volt
	// (motor velocity) = kV * (motor voltage)
	private final double kV;

	// Motor torque constant in newton-meter/ampere
	// (motor torque) = kT * (motor current)
	private final double kT;

	// Motor resistance is calculated as 12V / (stall current at 12V)
	private final double motorResistance;

	private ElectricalDevice.Output electricalState = new ElectricalDevice.Output(0);
	private MechanicalAngularDevice.Input mechanicalState = new MechanicalAngularDevice.Input(0);

	public DCMotor(double referenceVoltage, double freeSpeedRpm, double freeCurrentAmps, double stallTorqueNewtonMeters, double stallCurrentAmps) {
		this.motorResistance = referenceVoltage / stallCurrentAmps;
		this.kV = freeSpeedRpm / 60.0 * 2 * Math.PI / (referenceVoltage - motorResistance * freeCurrentAmps);
		this.kT = stallTorqueNewtonMeters / stallCurrentAmps;
	}

	@Override
	public MechanicalAngularDevice.Output step(final MechanicalAngularDevice.Input input) {
		mechanicalState = new MechanicalAngularDevice.Input(input);

		return new MechanicalAngularDevice.Output(kT * electricalState.current);
	}

	@Override
	public ElectricalDevice.Output step(final ElectricalDevice.Input input) {
		electricalState = new ElectricalDevice.Output((input.voltage - mechanicalState.angularVelocity / kV) / motorResistance);
		return electricalState;
	}
}
