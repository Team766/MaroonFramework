package com.team766.frc2022;

import java.util.ArrayList;
import javax.sql.rowset.serial.SerialArray;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.PositionReader;
import com.team766.logging.Severity;

public class Trajectory extends Procedure {
	//TODO:
	//1. Keep state of closest point. While dist to next point > lookAhead, increase i
	//2. interpolate between new two points
	//3. Write test case. (0, 0) -> (100, 0) -> (100, 100). At (60, 0) should start to see points going vertical, not just straight until (100,100) closer than (0, 0)

	private ArrayList<Pose> waypoints;
	public double LOOKAHEAD_DISTANCE =
			30;//ConfigFileReader.getInstance().getDouble("Trajectory.lookahead").get(); // inches
	private Pose currPose;

	public double TERMINATE_THRESHOLD = 5;
	private int currIndex = 0;
	private boolean done = false;

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

		while(distToPose(waypoints.get(currIndex)) < LOOKAHEAD_DISTANCE) {

			if(currIndex == waypoints.size()) {
				//TODO: Solve this case so that we actually continue to the point
				log("TOO CLOSE TO LAST POINT");
				break;
			}

			++currIndex;
		}

		if(distToPose(waypoints.get(currIndex)) < TERMINATE_THRESHOLD) {
			log("DONE");
			done = true;
			return null;
		}

		return _linearInterpolation(currIndex);
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
		if (closestPointIndex > 0) {
			lowerError = distToPose(waypoints.get(closestPointIndex - 1));
		} else {
			log(Severity.ERROR, "BAD STATE: start closer to a waypoint");
		}

		if(lowerError == Double.MAX_VALUE) {
			// just having 1 waypoint. Interpolate from current pose to desired
			lower = currPose;
			upper = waypoints.get(closestPointIndex);
		} else {
			lower = waypoints.get(closestPointIndex - 1);
			upper = waypoints.get(closestPointIndex);
		}

		/*
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
		double sqrtTerm = 0;
		if(dx != 0) {
			sqrtTerm = Math.sqrt(Math.pow(LOOKAHEAD_DISTANCE, 2) * Math.pow(dr, 2) - Math.pow(D, 2));
		}
		double newX1 = (D * dy + sign(dy) * dx * sqrtTerm) / Math.pow(dr, 2);
		double newX2 = (D * dy - sign(dy) * dx * sqrtTerm) / Math.pow(dr, 2);

		double newY1 = (-D * dx + Math.abs(dy) * sqrtTerm) / Math.pow(dr, 2);
		double newY2 = (-D * dx - Math.abs(dy) * sqrtTerm) / Math.pow(dr, 2);
		*/
		double A = (upper.m_y - lower.m_y) / (upper.m_x - lower.m_x);
      	double B = 1d + Math.pow(A, 2);
      	double C = (-2.0*currPose.m_x - 2.0*Math.pow(A, 2)*lower.m_x + 2.0*A*lower.m_y - 2.0*A*currPose.m_y);
      	double D = Math.pow(LOOKAHEAD_DISTANCE, 2) - Math.pow(currPose.m_x, 2) - Math.pow(A, 2)*Math.pow(lower.m_x, 2) + 2d*A*lower.m_x*lower.m_y - 2d*A*lower.m_x*currPose.m_y - Math.pow(lower.m_y, 2) + 2d*currPose.m_y*lower.m_y - Math.pow(currPose.m_y, 2);
      
      	double xResult1 = (-C + Math.sqrt(Math.pow(C, 2) + 4d*B*D)) / (2d*B);
      	double xResult2 = (-C - Math.sqrt(Math.pow(C, 2) + 4d*B*D)) / (2d*B);
      
      	double yResult1 = A*(xResult1-lower.m_x) + lower.m_y;
		double yResult2 = A*(xResult2-lower.m_x) + lower.m_y;
		  
		//TODO: handle case where robot is > L from the path
		// Two options - 1. fail because if we are that far away something went wrong
		// 				 2. Just find closest point on path to robot

		//if(Double.isNaN(newX1) || Double.isNaN(newX2) || Double.isNaN(newY1) || Double.isNaN(newY2)) {
		if(Double.isNaN(xResult1) || Double.isNaN(xResult2) || Double.isNaN(yResult1) || Double.isNaN(yResult2)) {
			log("A: %f, B: %f, C: %f, D: %f", A, B, C, D);
      		log("From %s -> %s", lower, upper);
      		log("Robot at %s -> (%f, %f) or (%f, %f)", currPose, xResult1, yResult1, xResult2, yResult2);
      		log("%f^2 = (%f - %f)^2 + (%f - %f)^2\n", LOOKAHEAD_DISTANCE, xResult1, currPose.m_x, yResult1, currPose.m_x);
		}

		//log("From %s to %s", lowerInRobotFrame.toString(), upperInRobotFrame.toString());
		//log("Pose: (%f, %f) (%f, %f)", newX1, newY1, newX2, newY2);

		//Pose option1 = new Pose(newX1 + currPose.m_x, newY1 + currPose.m_y, upper.m_heading);
		Pose option1 = new Pose(xResult1, yResult1, upper.m_heading);
		double option1Dist = distToPoseFromPose(option1, upper);
		//Pose option2 = new Pose(newX2 + currPose.m_x, newY2 + currPose.m_y, upper.m_heading);
		Pose option2 = new Pose(xResult2, yResult2, upper.m_heading);
		double option2Dist = distToPoseFromPose(option2, upper);

		//TODO: handle case where there's exactly 1 root
		//log("Interpolating: %s -> (%s or %s) -> %s", lower, option1, option2, upper);
		if(option1Dist < option2Dist) {
			return option1;
		}
		return option2;
	}

	public boolean isDone() {
		return done;
	}

	public void run(Context context) {
	}

}