package com.team766.frc2022.procedures;

import java.util.ArrayList;
import java.util.Arrays;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.frc2022.Pose;
import com.team766.frc2022.Robot;
import com.team766.frc2022.Trajectory;

public class TrajectoryFollower extends Procedure {

	private Trajectory trajectory;
	private double ROBOT_VELOCITY = 5; //ConfigFileReader.getInstance().getDouble("TrajectoryFollower.velocity").get();

	public TrajectoryFollower() {
		ArrayList<Pose> waypoints = new ArrayList<Pose>(Arrays.asList(new Pose(0, 0, 0), new Pose(100, 0, 0), new Pose(100, 100, 0), new Pose(0, 100, 0)));
		trajectory = new Trajectory(waypoints);
	}

	public void run(Context context) {
		context.takeOwnership(Robot.drive);
		Robot.drive.resetEncoders();
		Robot.drive.resetPose();

		int i = 0;
		while(!trajectory.isDone()) {
			//trajectory.setCurrPose(new Pose(0, 0, 0));
			//trajectory._linearInterpolation(1);
			Pose driveToPose = trajectory.findLookaheadPose();
			
			if(i > 500) {
				log("Drive to pose: %s -> %s", Robot.drive.getCurrPose(), driveToPose);
				i = 0;
			}
			++i;
			Robot.drive.setMotorPower(0.2, 0.2);
			context.yield();
		}
	}
	
}