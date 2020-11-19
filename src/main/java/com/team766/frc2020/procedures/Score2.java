package com.team766.frc2020.procedures;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.frc2020.Robot;

public class Score2 extends Procedure {
	public void run(Context context){
		context.takeOwnership(Robot.drive);
		context.takeOwnership(Robot.intake);
		context.takeOwnership(Robot.launcher);
		new StartIntake().run(context);
		new DriveStraight().run(context);
		new DriveStraight().run(context);
		new StopIntake().run(context);
		new driveSmall().run(context);
		new driveSmall().run(context);
		new driveSmall().run(context);
		context.takeOwnership(Robot.drive);
		Robot.drive.setDrivePower(0.25, 0.25);
		context.waitForSeconds(0.01);
		Robot.drive.setDrivePower(0, 0);

		new turnLeft().run(context);

		Robot.drive.setDrivePower(-0.25, 0.25);
		//adjust to set angle moving through color wheel
		context.waitForSeconds(0.0061);
		Robot.drive.setDrivePower(0, 0);
		new DriveStraight().run(context);
		new DriveStraight().run(context);
		new DriveStraight().run(context);

		/// Need to align with scoring
		context.takeOwnership(Robot.drive);
		Robot.drive.setDrivePower(-0.25, 0.25);
		// adjust to set aim
		context.waitForSeconds(0.1257);
		Robot.drive.setDrivePower(0, 0);
		///fire
		context.waitForSeconds(1);
		new Launch().run(context);
		new Launch().run(context);
		new Launch().run(context);
		new Launch().run(context);
		new Launch().run(context);
	}
}