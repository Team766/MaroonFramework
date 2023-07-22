package com.team766.simulator.interfaces;

public interface ElectricalDevice {
	class Input {
		public Input(final double voltageParam) {
			this.voltage = voltageParam;
		}

		public Input(final Input other) {
			voltage = other.voltage;
		}

		public final double voltage;
	}

	class Output {
		public Output(final double currentParam) {
			this.current = currentParam;
		}

		public Output(final Output other) {
			current = other.current;
		}

		public final double current;
	}

	Output step(Input input);
}
