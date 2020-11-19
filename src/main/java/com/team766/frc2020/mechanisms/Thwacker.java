package com.team766.frc2020.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.SpeedController; 
public class Thwacker extends Mechanism {
	private SpeedController m_thwacker;
	
	public Thwacker () {
		m_thwacker = RobotProvider.instance.getMotor("twacker");
	}
	public void thwacking (double speed){
		checkContextOwnership();
		m_thwacker.set(speed);
	}
}
