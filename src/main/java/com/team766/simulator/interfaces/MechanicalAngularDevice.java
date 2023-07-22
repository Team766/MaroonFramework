package com.team766.simulator.interfaces;

public interface MechanicalAngularDevice {
	class Input {
		public Input(final double angularVelocityParam) {
			this.angularVelocity = angularVelocityParam;
		}

		public Input(final Input other) {
			this.angularVelocity = other.angularVelocity;
		}

		public final double angularVelocity;
	}

	class Output {
		public Output(final double torqueParam) {
			this.torque = torqueParam;
		}

		public Output(final Output other) {
			this.torque = other.torque;
		}

		public final double torque;
	}

	Output step(Input input);
}
