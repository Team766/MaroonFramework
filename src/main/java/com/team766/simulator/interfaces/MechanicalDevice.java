package com.team766.simulator.interfaces;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public interface MechanicalDevice {
	class Input {
		public Input(final Vector3D positionParam, final Vector3D velocityParam) {
			this.position = positionParam;
			this.velocity = velocityParam;
		}

		public Input(final Input other) {
			position = other.position;
			velocity = other.velocity;
		}

		public final Vector3D position;
		public final Vector3D velocity;
	}

	class Output {
		public Output(final Vector3D forceParam) {
			this.force = forceParam;
		}

		public Output(final Output other) {
			force = other.force;
		}

		public final Vector3D force;
	}

	Output step(Input input);
}
