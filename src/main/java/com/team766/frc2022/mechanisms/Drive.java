package com.team766.frc2022.mechanisms;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.frc2022.Pose;
import com.team766.hal.RobotProvider;
import com.team766.hal.SpeedController;
import com.team766.hal.EncoderReader;
import com.team766.hal.GyroReader;
import com.team766.hal.PositionReader;

public class Drive extends Mechanism {
	private SpeedController m_leftMotor;
	private SpeedController m_rightMotor;

	private EncoderReader m_leftEncoder;
	private EncoderReader m_rightEncoder;

	private GyroReader m_gyro;

	private PositionReader m_positionReader;

	private double m_x;
	private double m_y;
	private double m_theta;

	private double m_prevLeftDistance;
	private double m_prevRightDistance;
	private double m_prevTheta;

	public static final double WHEEL_TRACK = 26.5; //in

	public Drive() {
		m_leftMotor = RobotProvider.instance.getMotor("drive.leftMotor");
		m_rightMotor = RobotProvider.instance.getMotor("drive.rightMotor");
		m_leftEncoder = RobotProvider.instance.getEncoder("drive.leftEncoder");
		m_rightEncoder = RobotProvider.instance.getEncoder("drive.rightEncoder");
		m_positionReader = RobotProvider.instance.getPositionSensor();

		m_gyro = RobotProvider.instance.getGyro("drive.gyro");

		m_prevLeftDistance = 0;
		m_prevRightDistance = 0;
		m_prevTheta = 0;

		m_x = m_y = m_theta = 0;
	}

	public void setMotorPower(double leftPower, double rightPower){
		checkContextOwnership();

		m_leftMotor.set(leftPower);
		m_rightMotor.set(rightPower);
	}

	public void resetEncoders() {
		log("start resetting");
		m_leftEncoder.reset();
		m_rightEncoder.reset();

		m_prevLeftDistance = 0;
		m_prevRightDistance = 0;
	}

	public void resetPose() {
		m_x = m_y = m_theta = 0;
		m_gyro.reset();
		log("done resetting");
	}

	public double getDistance() {
		return (m_leftEncoder.getDistance() + m_rightEncoder.getDistance()) / 2.0;
	}

	private double deltaX(double leftDeltaDist, double rightDeltaDist, double thetaDelta) {
		/*
		if(leftDeltaDist == rightDeltaDist) {
			return leftDeltaDist;
		}

		double numerator = WHEEL_TRACK * (leftDeltaDist + rightDeltaDist);
		double denominator = 2.0 * (rightDeltaDist - leftDeltaDist);
		return (numerator / denominator) * Math.sin(thetaDelta);
		*/

		//Small angle approximation
		return (leftDeltaDist + rightDeltaDist) / 2.0;
	}

	public Pose getCurrPose() {
		return new Pose(m_x, m_y, m_theta);
		//TODO: Revert this
		//return new Pose(metersToInches(m_positionReader.getX()), metersToInches(m_positionReader.getY()), degreesToRadians(m_positionReader.getHeading()) + Math.PI / 2d);
	}

	private double deltaY(double leftDeltaDist, double rightDeltaDist, double thetaDelta) {
		/*
		if(leftDeltaDist == rightDeltaDist) {
			return 0;
		}

		double numerator = WHEEL_TRACK * (leftDeltaDist + rightDeltaDist);
		double denominator = 2.0 * (rightDeltaDist - leftDeltaDist);
		return (numerator / denominator) * (1.0 - Math.cos(thetaDelta));
		*/

		//Small angle approximation
		return 0;
	}

	private double deltaTheta(double leftDeltaDist, double rightDeltaDist) {
		// double currDelta = (rightDeltaDist - leftDeltaDist) / WHEEL_TRACK;
		// if(currDelta != 0)
		// 	log("Delta: %f", currDelta);
		double delta = rightDeltaDist - leftDeltaDist;
		return delta / WHEEL_TRACK;
	}

	private void updatePose() {
		double currLeftDistance = m_leftEncoder.getDistance();
		double currRightDistance = m_rightEncoder.getDistance();
		double currTheta = degreesToRadians(m_gyro.getAngle());
		double currLeftDelta = currLeftDistance - m_prevLeftDistance;
		double currRightDelta = currRightDistance - m_prevRightDistance;
		double currThetaDelta = currTheta - m_prevTheta;

		if(currLeftDelta == 0 && currRightDelta == 0) {
			return;
		}

		double deltaX = deltaX(currLeftDelta, currRightDelta, currThetaDelta);
		double deltaY = deltaY(currLeftDelta, currRightDelta, currThetaDelta);
		
		double xNew = m_x + deltaX * Math.cos(m_theta) - deltaY * Math.sin(m_theta);
		double yNew = m_y + deltaX * Math.sin(m_theta) - deltaY * Math.cos(m_theta); 
		
		//log("%f vs %f", radiansToDegrees(deltaTheta(currLeftDelta, currRightDelta)), m_gyro.getAngle());
		m_theta += currThetaDelta; //deltaTheta(currLeftDelta, currRightDelta);
		m_x = xNew;
		m_y = yNew;

		m_prevLeftDistance = currLeftDistance;
		m_prevRightDistance = currRightDistance;
		m_prevTheta = currTheta;
	}

	public double getHeading() {
		return m_theta;
	}

	private double metersToInches(double distance) {
		return distance * 39.3701;
	}

	private double inchesToMeters(double distance) {
		return distance * 0.0254;
	}

	private double radiansToDegrees(double rads) {
		return rads * (180d / Math.PI);
	}

	private double degreesToRadians(double degrees) {
		return degrees * (Math.PI / 180d);
	}

	public void setVelocities(double vLeft, double vRight) {
		double speedScalar = 0.1; //ConfigFileReader.getInstance().getDouble("Drive.VELOCITY_SCALAR").get();
		m_leftMotor.set(vLeft * speedScalar);
		m_rightMotor.set(vRight * speedScalar);
	}

	public void run() {
		updatePose();

		//log("(%f, %f, %f) vs absolute: (%f, %f, %f) dist: %f, %f, %f", inchesToMeters(m_x), inchesToMeters(m_y), radiansToDegrees(m_theta), m_positionReader.getX(), m_positionReader.getY(), m_positionReader.getHeading(), inchesToMeters(m_leftEncoder.getDistance()), inchesToMeters(m_rightEncoder.getDistance()), m_gyro.getAngle());
	}
}