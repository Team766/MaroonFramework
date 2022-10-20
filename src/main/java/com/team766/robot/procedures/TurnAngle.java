package com.team766.robot.procedures;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.robot.Robot;

public class TurnAngle extends Procedure {

	private double targetAngle;

	public TurnAngle(double targetAngle) {
		this.targetAngle = targetAngle;
	}

	public void run(Context context) {
		context.takeOwnership(Robot.drive);

		Robot.drive.resetGyro();

		Robot.drive.setDrivePower(0.0);
		Robot.drive.setTargetHeading(targetAngle);
		context.waitFor(() -> Robot.drive.atAngle());

		// Wait for robot to come to a stop.
		context.waitForSeconds(0.5);
	}

}
