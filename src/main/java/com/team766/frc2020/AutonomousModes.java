package com.team766.frc2020;

import com.team766.framework.AutonomousProcedure;
import com.team766.frc2020.procedures.*;

public enum AutonomousModes {
	@AutonomousProcedure(procedureClass = ScoreEasy.class)
	AutonScore,
	@AutonomousProcedure(procedureClass = driveSmall.class)
	driveSmall,
	@AutonomousProcedure(procedureClass = square.class)
	square,
	@AutonomousProcedure(procedureClass = DriveStraight.class)
	DriveStraight,
	@AutonomousProcedure(procedureClass = turnRight.class)
	turnRight,
	@AutonomousProcedure(procedureClass = Launch.class)
	Launch,
	@AutonomousProcedure(procedureClass = turnLeft.class)
	turnLeft,
}
