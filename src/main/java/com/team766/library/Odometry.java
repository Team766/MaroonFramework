package com.team766.library;

import com.team766.framework.LoggingBase;
import com.team766.math.PointDir;
import com.team766.hal.MotorController;
import com.ctre.phoenix.sensors.CANCoder;
import com.team766.logging.Category;
import com.team766.robot.*;

public class Odometry extends LoggingBase {

	private RateLimiter odometryLimiter;

	//Each successive motor should be adjacent to previous on robot
	private MotorController[] motorList;
	//The order of CANCoders should be the same as in motorList
	private CANCoder[] CANCoderList;
	private	int motorCount; 

	private PointDir[] prevPositions;
	private PointDir[] currPositions;
	private double[] prevEncoderValues;
	private double[] currEncoderValues;
	private double gyroPosition;

	private PointDir currentPosition;

	//In Centimeters
	private static final double WHEEL_CIRCUMFERENCE = 11.0446616728 * 2.54;
	public static final double GEAR_RATIO = 6.75;
	public static final int ENCODER_TO_REVOLUTION_CONSTANT = 2048;

	public static final double DISTANCE_BETWEEN_WHEELS = 24 * 2.54;

	public Odometry(MotorController[] motors, CANCoder[] CANCoders, double rateLimiterTime) {
		loggerCategory = Category.ODOMETRY;

		odometryLimiter = new RateLimiter(rateLimiterTime);
		motorList = motors;
		CANCoderList = CANCoders;
		motorCount = motorList.length;
		log("Motor count " + motorCount);
		prevPositions = new PointDir[motorCount];
		currPositions = new PointDir[motorCount];
		prevEncoderValues = new double[motorCount];
		currEncoderValues = new double[motorCount];

		currentPosition = new PointDir(0, 0, 0);
		for (int i = 0; i < motorCount; i++) {
			prevPositions[i] = new PointDir(0,0, 0);
			currPositions[i] = new PointDir(0,0, 0);
			prevEncoderValues[i] = 0;
			currEncoderValues[i] = 0;
		}
	}

	public String getName() {
		return "Odometry";
	}

	public void resetCurrentPosition() {
		currentPosition.set(0, 0);
		for (int i = 0; i < motorCount; i++) {
			prevPositions[i].set(0,0);
			currPositions[i].set(0,0);
		}
	}

	private void setCurrentEncoderValues() {
		for (int i = 0; i < motorCount; i++) {
			prevEncoderValues[i] = currEncoderValues[i];
			currEncoderValues[i] = motorList[i].getSensorPosition();
		}
	}

	private void updateCurrentPositions() {
		double angleChange;
		double radius;
		double deltaX;
		double deltaY;
		gyroPosition = Robot.gyro.getGyroYaw();

		for (int i = 0; i < motorCount; i++) {
			//Note: I haven't decided whether or not to add this. It assumes the wheels form a regular polygon, with the first point in the top-right
			prevPositions[i] = new PointDir(currentPosition.getX() + 0.5 * DISTANCE_BETWEEN_WHEELS / Math.sin(Math.PI / motorCount) * Math.cos(currentPosition.getHeading() + ((Math.PI + 2 * Math.PI * i) / motorCount)), currentPosition.getY() + 0.5 * DISTANCE_BETWEEN_WHEELS / Math.sin(Math.PI / motorCount) * Math.sin(currentPosition.getHeading() + ((Math.PI + 2 * Math.PI * i) / motorCount)), currPositions[i].getHeading());

			currPositions[i].setHeading(CANCoderList[i].getAbsolutePosition() + gyroPosition);
			angleChange = currPositions[i].getHeading() - prevPositions[i].getHeading();
			if (angleChange != 0) {
				radius = 180 * (currEncoderValues[i] - prevEncoderValues[i]) / (Math.PI * angleChange);
				deltaX = radius * Math.sin(Math.toRadians(angleChange));
				deltaY = radius * (1 - Math.cos(Math.toRadians(angleChange)));
				currPositions[i].setX(prevPositions[i].getX() + (Math.cos(Math.toRadians(prevPositions[i].getHeading())) * deltaX - Math.sin(Math.toRadians(prevPositions[i].getHeading())) * deltaY) * WHEEL_CIRCUMFERENCE / (GEAR_RATIO * ENCODER_TO_REVOLUTION_CONSTANT));
				currPositions[i].setY(prevPositions[i].getY() + (Math.sin(Math.toRadians(prevPositions[i].getHeading())) * deltaX + Math.cos(Math.toRadians(prevPositions[i].getHeading())) * deltaY) * WHEEL_CIRCUMFERENCE / (GEAR_RATIO * ENCODER_TO_REVOLUTION_CONSTANT));
			} else {
				currPositions[i].setX(prevPositions[i].getX() + (currEncoderValues[i] - prevEncoderValues[i]) * Math.cos(Math.toRadians(prevPositions[i].getHeading())) * WHEEL_CIRCUMFERENCE / (GEAR_RATIO * ENCODER_TO_REVOLUTION_CONSTANT));
				currPositions[i].setY(prevPositions[i].getY() + (currEncoderValues[i] - prevEncoderValues[i]) * Math.sin(Math.toRadians(prevPositions[i].getHeading())) * WHEEL_CIRCUMFERENCE / (GEAR_RATIO * ENCODER_TO_REVOLUTION_CONSTANT));
			}
		}
	}

	private void findRobotPosition() {
		double avgX = 0;
		double avgY = 0;
		for (int i = 0; i < motorCount; i++) {
			avgX += currPositions[i].getX();
			avgY += currPositions[i].getY();
		}
		currentPosition.set(avgX / motorCount, avgY / motorCount, gyroPosition);
	}

	//Intended to be placed inside Robot.drive.run()
	public PointDir run() {
		if (odometryLimiter.next()) {
			setCurrentEncoderValues();
			updateCurrentPositions();
			findRobotPosition();
		}
		return currentPosition;
	}
}
