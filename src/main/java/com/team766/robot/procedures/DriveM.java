package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.logging.Category;
import com.team766.robot.Robot;

public class DriveM extends Procedure {

	private static final double SIDE_LENGTH = 60;
	private static final double CORNER_ANGLE = 40;

	public DriveM() {
		loggerCategory = Category.AUTONOMOUS;
	}

	public void run(Context context) {
		context.takeOwnership(Robot.drive);
		Robot.drive.resetGyro();

		// First side
		new DriveDistance(SIDE_LENGTH).run(context);
		log("First side complete");

		// First corner
		new TurnAngle(CORNER_ANGLE - 180).run(context);
		log("First corner complete");

		// Second side
		new DriveDistance(SIDE_LENGTH).run(context);
		log("Second side complete");

		// Second corner
		new TurnAngle(180 - 2 * CORNER_ANGLE).run(context);
		log("Second corner complete");

		// Third side
		new DriveDistance(SIDE_LENGTH).run(context);
		log("Third side complete");

		// Third corner
		new TurnAngle(CORNER_ANGLE - 180).run(context);
		log("Third corner complete");

		// Fourth side
		new DriveDistance(SIDE_LENGTH).run(context);
		log("Fourth side complete");
	}

}