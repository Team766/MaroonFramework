package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.hal.wpilib.Solenoid;

public class Intake extends Mechanism {
	
	private MotorController bottomWheels;
	private MotorController topBelt;
	private SolenoidController leftPiston;
	private SolenoidController rightPiston;
	
	public Intake() {
		topBelt = RobotProvider.instance.getMotor("Intake.topWheels");
		bottomWheels = RobotProvider.instance.getMotor("Intake.bottomWheels");

		leftPiston = RobotProvider.instance.getSolenoid("Intake.leftPiston");
		rightPiston = RobotProvider.instance.getSolenoid("Intake.rightPiston");

	}

	public void startIntake() {
		checkContextOwnership();
		
		pistonsOut();
		topBelt.set(1.0);
		bottomWheels.set(1.0);

	}

	public void stopIntake() {
		checkContextOwnership();
		
		topBelt.set(0.0);
		bottomWheels.set(0.0);
		pistonsIn();
	}

	public void pistonsOut() {
		checkContextOwnership();

		leftPiston.set(true);
		rightPiston.set(true);
	}

	public void pistonsIn() {
		checkContextOwnership();

		leftPiston.set(false);
		rightPiston.set(false);
	}

	public void reverseIntake() {
		checkContextOwnership();

		topBelt.set(-1.0);
		bottomWheels.set(-1.0);

		pistonsIn();
	}
}