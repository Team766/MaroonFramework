package com.team766.frc2020.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.SpeedController; 
import com.team766.hal.SolenoidController;

public class Intake extends Mechanism {
	private SolenoidController intakeArm;
	private SpeedController intakeWheels;
	public Intake() {
		intakeWheels = RobotProvider.instance.getMotor("intakeWheels");
		intakeArm = RobotProvider.instance.getSolenoid("intakeArm");
	}
	public void startIntake() {

		intakeArm.set(true);
		intakeWheels.set(1);
	}
		public void stopIntake() {
		intakeArm.set(false);
		intakeWheels.set(0);
	}
}