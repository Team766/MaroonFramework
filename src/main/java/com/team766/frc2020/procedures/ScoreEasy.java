package com.team766.frc2020.procedures;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.frc2020.Robot;

public class ScoreEasy extends Procedure {
	public void run(Context context){
		context.takeOwnership(Robot.drive);
		context.takeOwnership(Robot.intake);
		context.takeOwnership(Robot.launcher);
		new StartIntake().run(context);
		new DriveStraight().run(context);
		new StopIntake().run(context);
		context.takeOwnership(Robot.drive);
		Robot.drive.setDrivePower(-0.25, -0.25);
		context.waitForSeconds(2.7);
		Robot.drive.setDrivePower(0.0, 0.0);
		new turnRight().run(context);
		///fire
		context.takeOwnership(Robot.drive);
		Robot.drive.setDrivePower(-0.25, 0.25);
		context.waitForSeconds(0.03);
		Robot.drive.setDrivePower(0.0, 0.0);
		context.waitForSeconds(1);
		new Launch().run(context);
		new Launch().run(context);
		new Launch().run(context);
		new Launch().run(context);
		new Launch().run(context);
	}
}