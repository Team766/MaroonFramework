package com.team766.simulator.interfaces;

public interface MechanicalAngularDevice {
	class Input {
		public Input(final double angularVelocity_) {
			this.angularVelocity = angularVelocity_;
		}

		public Input(final Input other) {
			this.angularVelocity = other.angularVelocity;
		}

		public final double angularVelocity;
	}

	class Output {
		public Output(final double torque_) {
			this.torque = torque_;
		}

		public Output(final Output other) {
			this.torque = other.torque;
		}

		public final double torque;
	}

	Output step(Input input);
}
