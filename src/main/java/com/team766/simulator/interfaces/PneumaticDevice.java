package com.team766.simulator.interfaces;

public interface PneumaticDevice {
    class Input {
        public Input(final double pressure_) {
            this.pressure = pressure_;
        }

        public Input(final Input other) {
            pressure = other.pressure;
        }

        // Pascals (relative pressure)
        public final double pressure;
    }

    class Output {
        public Output(final double flowVolume_, final double deviceVolume_) {
            this.flowVolume = flowVolume_;
            this.deviceVolume = deviceVolume_;
        }

        public Output(final Output other) {
            flowVolume = other.flowVolume;
            deviceVolume = other.deviceVolume;
        }

        // Volumetric flow (delta m^3 at atmospheric pressure)
        // Positive flow is into the system, e.g. from a compressor
        // Negative flow is out of the system, e.g. from venting to atmosphere
        public final double flowVolume;

        // Volume of air that the device contains (m^3)
        public final double deviceVolume;

        // Note that an expanding volume (such as a cylinder expanding)
        // should increase volume, but have 0 flow volume because no
        // pressurized air is actually leaving the system.
    }

    public Output step(Input input, double dt);
}
