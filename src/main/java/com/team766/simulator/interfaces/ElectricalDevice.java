package com.team766.simulator.interfaces;

public interface ElectricalDevice {
	class Input {
		public Input(final double voltage_) {
			this.voltage = voltage_;
		}

		public Input(final Input other) {
			voltage = other.voltage;
		}

		public final double voltage;
	}

	class Output {
		public Output(final double current_) {
			this.current = current_;
		}

		public Output(final Output other) {
			current = other.current;
		}

		public final double current;
	}

	Output step(Input input);
}
