package com.team766.frc2020.procedures;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.frc2020.Robot;

public class Score extends Procedure {
	public void run(Context context){
		context.takeOwnership(Robot.drive);
		context.takeOwnership(Robot.intake);
		context.takeOwnership(Robot.launcher);
		new turnLeft().run(context);
		context.takeOwnership(Robot.drive);
		Robot.drive.setDrivePower(-0.25, 0.25);
		context.waitForSeconds(0.1);
		Robot.drive.setDrivePower(0, 0);
		new DriveStraight().run(context);
		new DriveStraight().run(context);
		new turnRight().run(context);
	//Now at middle field element
		new DriveStraight().run(context);
		new DriveStraight().run(context);
		new DriveStraight().run(context);
		context.takeOwnership(Robot.drive);
		Robot.drive.setDrivePower(1, 1);
		context.waitForSeconds(0.2);
		Robot.drive.setDrivePower(0, 0);
		//exited middle field element
		new turnLeft().run(context);
		new DriveStraight().run(context);
		new DriveStraight().run(context);
		/// Need to align with scoring
		context.takeOwnership(Robot.drive);
		Robot.drive.setDrivePower(-0.25, 0.25);
		context.waitForSeconds(0.1);
		Robot.drive.setDrivePower(0, 0);
		///fire
		context.waitForSeconds(0.1);
		new Launch().run(context);
		new Launch().run(context);
		new Launch().run(context);
	}
}