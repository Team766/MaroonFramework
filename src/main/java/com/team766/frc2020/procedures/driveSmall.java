package com.team766.frc2020.procedures;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.frc2020.Robot;

public class driveSmall extends Procedure {
	public void run(Context context){
		context.takeOwnership(Robot.drive);
		Robot.drive.setDrivePower(0.25, 0.25);
		context.waitForSeconds(0.5);
		Robot.drive.setDrivePower(0.0, 0.0);
	}
}