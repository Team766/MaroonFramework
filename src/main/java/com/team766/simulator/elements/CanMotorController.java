package com.team766.simulator.elements;

import com.team766.simulator.ProgramInterface;
import com.team766.simulator.interfaces.ElectricalDevice;

public class CanMotorController extends MotorController {

	private int address;

	public CanMotorController(final int addressParam, final ElectricalDevice downstream) {
		super(downstream);
		this.address = addressParam;
	}

	@Override
	protected double getCommand() {
		return ProgramInterface.canMotorControllerChannels[address].command.output;
	}

}
