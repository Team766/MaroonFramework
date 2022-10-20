package com.team766.robot.procedures;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.robot.Robot;
import com.team766.logging.Category;

public class AutoShoot extends Procedure{
	public AutoShoot() {
		loggerCategory = Category.AUTONOMOUS;
	}

	public void run(Context context) {
		context.takeOwnership(Robot.shooter);
		context.takeOwnership(Robot.belts);

		Robot.shooter.setVelocity(3000);
		context.waitForSeconds(5);
		Robot.belts.startBelts();
		context.waitForSeconds(5);
		Robot.belts.stopBelts();
		Robot.shooter.stopShoot();
        
	}
}

