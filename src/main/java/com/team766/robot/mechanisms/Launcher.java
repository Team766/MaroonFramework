package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;

public class Launcher extends Mechanism {
	private SolenoidController m_plungerSolenoid;

	public Launcher() {
		m_plungerSolenoid = RobotProvider.instance.getSolenoid("launch");
	}

	public void setPlunger(boolean state) {
		checkContextOwnership();
		m_plungerSolenoid.set(state);
	}
}