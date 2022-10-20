package com.team766.robot.mechanisms;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Belts extends Mechanism {
	private MotorController m_leftStorageBelt;
	private MotorController m_rightStorageBelt;
	
	public Belts() {
		m_leftStorageBelt = RobotProvider.instance.getMotor("belt.left");
		m_rightStorageBelt = RobotProvider.instance.getMotor("belt.right");
		m_rightStorageBelt.setInverted(true);
		//loggerCategory = 
	}

	public void startBelts() {
		checkContextOwnership();
		double power = ConfigFileReader.getInstance().getDouble("belt.beltPower").valueOr(1.0);
		log("setting them to config value");
		m_leftStorageBelt.set(power);
		m_rightStorageBelt.set(power);
	}

	public void stopBelts() {
		checkContextOwnership();
		log("stopping them 0.0");
		m_leftStorageBelt.set(0.0);
		m_rightStorageBelt.set(0.0);
	}

	public void reverseBelts(){
		checkContextOwnership();
		double power = ConfigFileReader.getInstance().getDouble("belt.beltPower").valueOr(1.0);

		m_leftStorageBelt.set(-power);
		m_rightStorageBelt.set(-power);
	}
}
