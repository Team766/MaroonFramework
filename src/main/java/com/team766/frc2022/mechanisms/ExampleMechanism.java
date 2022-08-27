package com.team766.frc2022.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.SpeedController;

import com.ctre.phoenix.led.CANdle;


public class ExampleMechanism extends Mechanism {
	//private SpeedController m_leftMotor;
	//private SpeedController m_rightMotor;
	private CANdle candle;

	public ExampleMechanism() {
		//m_leftMotor = RobotProvider.instance.getCANMotor("exampleMechanism.leftMotor");
		//m_rightMotor = RobotProvider.instance.getCANMotor("exampleMechanism.rightMotor");
		candle = new CANdle(0); //ID is 0 right now
	}

	public void setCANdle(int R, int G, int B){
		checkContextOwnership();
		candle.setLEDs(R, G, B);
		//m_leftMotor.set(leftPower);
		//m_rightMotor.set(rightPower);
	}
}