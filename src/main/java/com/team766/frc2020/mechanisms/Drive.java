package com.team766.frc2020.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.SpeedController; 

public class Drive extends Mechanism {
	private SpeedController m_leftmotor;
	private SpeedController m_rightmotor;

	public Drive () {
		m_leftmotor = RobotProvider.instance.getMotor("drive.leftMotor");
		m_rightmotor = RobotProvider.instance.getMotor("drive.rightMotor");

	}
	public void arcadeDrive(double foward, double turn){
		double leftpower = foward + turn;
		double rightpower = foward + -turn;
		setDrivePower(leftpower, rightpower);

	}
	public void setDrivePower(double leftpower, double rightpower){
		checkContextOwnership();
		m_leftmotor.set(leftpower);
		m_rightmotor.set(rightpower);
	}
	
}

