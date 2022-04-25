package com.team766.frc2022;

import java.util.ArrayList;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.PositionReader;
import com.team766.logging.Severity;

public class Trajectory extends Procedure {
	private ArrayList<Pose> waypoints;
	private double LOOKAHEAD_DISTANCE =
			30;//ConfigFileReader.getInstance().getDouble("Trajectory.lookahead").get(); // inches
	private Pose currPose;

	public Trajectory(ArrayList<Pose> _waypoints) {
		waypoints = _waypoints;
	}

	public Pose getNextPoint() {

		return null;
	}

	public void setCurrPose(Pose p) {
		currPose = p;
	}

	private double distToPose(Pose p) {
		return distToPoseFromPose(p, currPose);
	}

	private double distToPoseFromPose(Pose p1, Pose p2) {
		double xPow = Math.pow(p1.m_x - p2.m_x, 2);
		double yPow = Math.pow(p1.m_y - p2.m_y, 2);
		return Math.sqrt(xPow + yPow);
	}

	public Pose findLookaheadPose() {
		currPose = Robot.drive.getCurrPose();

		int closestPoint = 0;
		double minError = Double.MAX_VALUE;
		for (int i = 0; i < waypoints.size(); ++i) {
			Pose thisPose = waypoints.get(i);
			double error = Math.abs(distToPose(thisPose) - LOOKAHEAD_DISTANCE);

			if (error < minError) {
				closestPoint = i;
				minError = error;
			}
		}

		if (minError == 0) {
			// This will rarely happen
			return waypoints.get(closestPoint);
		}

		return _linearInterpolation(closestPoint);
	}

	// Sign function, but 0 = 1
	private double sign(double val) {
		if (val < 0) {
			return -1;
		}
		return 1;
	}

	public Pose _linearInterpolation(int closestPointIndex) {
		Pose lower;
		Pose upper;

		double lowerError = Double.MAX_VALUE;
		double upperError = Double.MAX_VALUE;

		if (closestPointIndex > 0) {
			lowerError = distToPose(waypoints.get(closestPointIndex - 1));
		}
		if (closestPointIndex < waypoints.size() - 1) {
			upperError = distToPose(waypoints.get(closestPointIndex + 1));
		}

		if (lowerError < upperError) {
			lower = waypoints.get(closestPointIndex - 1);
			upper = waypoints.get(closestPointIndex);
		} else if (lowerError > upperError) {
			lower = waypoints.get(closestPointIndex);
			upper = waypoints.get(closestPointIndex + 1);
		} else {
			// just having 1 waypoint. Interpolate from current pose to desired
			lower = currPose;
			upper = waypoints.get(closestPointIndex);
		}

		// Find intersection of circle around the robot and the line
		// Center about the robot position
		Pose lowerInRobotFrame =
				new Pose(lower.m_x - currPose.m_x, lower.m_y - currPose.m_y, lower.m_heading);
		Pose upperInRobotFrame =
				new Pose(upper.m_x - currPose.m_x, upper.m_y - currPose.m_y, upper.m_heading);

		double dx = upperInRobotFrame.m_x - lowerInRobotFrame.m_x;
		double dy = upperInRobotFrame.m_y - lowerInRobotFrame.m_y;
		double dr = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		double D = lowerInRobotFrame.m_x * upperInRobotFrame.m_y
				- upperInRobotFrame.m_x * lowerInRobotFrame.m_y;

		// In robot frame
		double newX1 = (D * dy + sign(dy) * dx
				* Math.sqrt(Math.pow(LOOKAHEAD_DISTANCE, 2) * Math.pow(dr, 2) - Math.pow(D, 2)))
				/ Math.pow(dr, 2);
		double newX2 = (D * dy - sign(dy) * dx
				* Math.sqrt(Math.pow(LOOKAHEAD_DISTANCE, 2) * Math.pow(dr, 2) - Math.pow(D, 2)))
				/ Math.pow(dr, 2);

		double newY1 = (-D * dx + Math.abs(dy)
				* Math.sqrt(Math.pow(LOOKAHEAD_DISTANCE, 2) * Math.pow(dr, 2) - Math.pow(D, 2)))
				/ Math.pow(dr, 2);
		double newY2 = (-D * dx - Math.abs(dy)
				* Math.sqrt(Math.pow(LOOKAHEAD_DISTANCE, 2) * Math.pow(dr, 2) - Math.pow(D, 2)))
				/ Math.pow(dr, 2);

		//log("From %s to %s", lowerInRobotFrame.toString(), upperInRobotFrame.toString());
		//log("Pose: (%f, %f) (%f, %f)", newX1, newY1, newX2, newY2);

		Pose option1 = new Pose(newX1 + currPose.m_x, newY1 + currPose.m_y, upper.m_heading);
		double option1Dist = distToPoseFromPose(option1, upper);
		Pose option2 = new Pose(newX2 + currPose.m_x, newY2 + currPose.m_y, upper.m_heading);
		double option2Dist = distToPoseFromPose(option2, upper);

		if(option1Dist < option2Dist) {
			return option1;
		}
		return option2;
	}

	public boolean isDone() {
		return false;
	}

	public void run(Context context) {
	}

}