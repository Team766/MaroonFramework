package com.team766.frc2022.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.SpeedController;
import com.team766.hal.EncoderReader;
import com.team766.hal.PositionReader;

public class Drive extends Mechanism {
	private SpeedController m_leftMotor;
	private SpeedController m_rightMotor;

	private EncoderReader m_leftEncoder;
	private EncoderReader m_rightEncoder;

	private PositionReader m_positionReader;

	private double m_x;
	private double m_y;
	private double m_theta;

	private double m_prevLeftDistance;
	private double m_prevRightDistance;

	private static final double WHEEL_TRACK = 26.5; //in

	public Drive() {
		m_leftMotor = RobotProvider.instance.getMotor("drive.leftMotor");
		m_rightMotor = RobotProvider.instance.getMotor("drive.rightMotor");
		m_leftEncoder = RobotProvider.instance.getEncoder("drive.leftEncoder");
		m_rightEncoder = RobotProvider.instance.getEncoder("drive.rightEncoder");
		m_positionReader = RobotProvider.instance.getPositionSensor();

		m_prevLeftDistance = 0;
		m_prevRightDistance = 0;

		m_x = m_y = m_theta = 0;
	}

	public void setMotorPower(double leftPower, double rightPower){
		checkContextOwnership();

		m_leftMotor.set(leftPower);
		m_rightMotor.set(rightPower);
	}

	public void resetEncoders() {
		m_leftEncoder.reset();
		m_rightEncoder.reset();

		m_prevLeftDistance = 0;
		m_prevRightDistance = 0;
	}

	public void restPose() {
		m_x = m_y = m_theta = 0;
	}

	public double getDistance() {
		return (m_leftEncoder.getDistance() + m_rightEncoder.getDistance()) / 2.0;
	}

	private double deltaX(double leftDeltaDist, double rightDeltaDist) {
		return (leftDeltaDist + rightDeltaDist) / 2.0;
	}

	private double deltaY() {
		return 0;
	}

	private double deltaTheta(double leftDeltaDist, double rightDeltaDist) {
		return (rightDeltaDist - leftDeltaDist) / WHEEL_TRACK;
	}

	private void updatePose() {
		double currLeftDistance = m_leftEncoder.getDistance();
		double currRightDistance = m_rightEncoder.getDistance();
		double currLeftDelta = currLeftDistance - m_prevLeftDistance;
		double currRightDelta = currRightDistance - m_prevRightDistance;

		double deltaX = deltaX(currLeftDelta, currRightDelta);
		double deltaY = deltaY();
		
		double xNew = m_x + deltaX * Math.cos(m_theta) - deltaY * Math.sin(m_theta);
		double yNew = m_y + deltaX * Math.sin(m_theta) - deltaY * Math.cos(m_theta); 
		
		m_theta += deltaTheta(currLeftDelta, currRightDelta);
		m_x = xNew;
		m_y = yNew;

		m_prevLeftDistance = currLeftDistance;
		m_prevRightDistance = currRightDistance;
	}

	private double inchesToMeters(double distance) {
		return distance * 0.0254;
	}

	private double radiansToDegrees(double rads) {
		return rads * (180d / Math.PI);
	}

	public void run() {
		updatePose();

		log("(%f, %f, %f) vs absolute: (%f, %f, %f)", inchesToMeters(m_x), inchesToMeters(m_y), radiansToDegrees(m_theta), m_positionReader.getX(), m_positionReader.getY(), m_positionReader.getHeading());
	}
}