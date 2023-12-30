package com.team766.simulator.interfaces;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public interface MechanicalDevice {
	class Input {
		public Input(final Vector3D position_, final Vector3D velocity_) {
			this.position = position_;
			this.velocity = velocity_;
		}

		public Input(final Input other) {
			position = other.position;
			velocity = other.velocity;
		}

		public final Vector3D position;
		public final Vector3D velocity;
	}

	class Output {
		public Output(final Vector3D force_) {
			this.force = force_;
		}

		public Output(final Output other) {
			force = other.force;
		}

		public final Vector3D force;
	}
	
	public Output step(Input input, double dt);
}
