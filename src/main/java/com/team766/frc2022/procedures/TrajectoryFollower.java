package com.team766.frc2022.procedures;

import java.util.ArrayList;
import java.util.Arrays;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.frc2022.Pose;
import com.team766.frc2022.Robot;
import com.team766.frc2022.Trajectory;
import com.team766.frc2022.mechanisms.Drive;

public class TrajectoryFollower extends Procedure {

	private Trajectory trajectory;
	private double ROBOT_VELOCITY = 1; //ConfigFileReader.getInstance().getDouble("TrajectoryFollower.velocity").get();

	public TrajectoryFollower() {
		ArrayList<Pose> waypoints = new ArrayList<Pose>(Arrays.asList(new Pose(0, 0, 0), new Pose(100, 0, 0), new Pose(150, 100, 0), new Pose(50, 100, 0), new Pose(0, 0, 0)));
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
			Pose currPose = Robot.drive.getCurrPose();
			Pose driveToPose = trajectory.findLookaheadPose();

			if(null == driveToPose) {
				break;
			}
			
			//Robot.drive.setMotorPower(0.2, 0.2);

			double deltaX = driveToPose.m_x - currPose.m_x;
			double deltaY = driveToPose.m_y - currPose.m_y;

			//double radius = Math.pow(trajectory.LOOKAHEAD_DISTANCE, 2) / (2 * deltaX);
			//double leftVelocity = (ROBOT_VELOCITY * (radius + (Drive.WHEEL_TRACK / 2.0))) / radius;
			//double rightVelocity = (ROBOT_VELOCITY * (radius - (Drive.WHEEL_TRACK / 2.0))) / radius;

			Pose robotVector = new Pose(Math.cos(currPose.m_heading), Math.sin(currPose.m_heading), currPose.m_heading); //unit vector in robot frame
			double crossProduct = (robotVector.m_x * deltaY) - (deltaX * robotVector.m_y); // (Ax * By) - (Bx * Ay)
			
			/*
			double waypointHeading = Math.atan(deltaY / deltaX);
			double deltaHeading = waypointHeading - currPose.m_heading;
			final double VELOCITY_PROPORTION = 0.9;//ConfigFileReader.getInstance().getDouble("TrajectoryFollower.VELOCITY_PROPORTION").get();
			double leftVelocity = ROBOT_VELOCITY - VELOCITY_PROPORTION * deltaHeading;
			double rightVelocity = ROBOT_VELOCITY + VELOCITY_PROPORTION * deltaHeading;
			Robot.drive.setVelocities(leftVelocity, rightVelocity);
			*/

			final double VELOCITY_PROPORTION = 0.08;
			double leftVelocity = ROBOT_VELOCITY - VELOCITY_PROPORTION * crossProduct;
			double rightVelocity = ROBOT_VELOCITY + VELOCITY_PROPORTION * crossProduct;
			Robot.drive.setVelocities(leftVelocity, rightVelocity);

			if(i > 500) {
				//log("%s x (%f, %f) = %f", robotVector, deltaX, deltaY, crossProduct);
				//log("Applying (%f, %f) +- %f %f to %s -> %s", leftVelocity, rightVelocity, crossProduct, VELOCITY_PROPORTION * crossProduct, currPose, driveToPose);

				i = 0;
			}
			++i;

			if(Double.isNaN(crossProduct)) {
				log("%s x (%f, %f) = %f", robotVector, deltaX, deltaY, crossProduct);
				log("Applying (%f, %f) +- %f %f to %s -> %s", leftVelocity, rightVelocity, crossProduct, VELOCITY_PROPORTION * crossProduct, currPose, driveToPose);
				break;
			}

			context.yield();
		}

		Robot.drive.setVelocities(0, 0);
	}
	
}