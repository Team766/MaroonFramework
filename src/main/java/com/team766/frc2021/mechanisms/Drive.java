package com.team766.frc2021.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.EncoderReader;
import com.team766.hal.GyroReader;
import com.team766.hal.RobotProvider;
import com.team766.hal.SpeedController;
import com.team766.logging.Category;

public class Drive extends Mechanism {
    private SpeedController m_leftMotor;
    private SpeedController m_rightMotor;
    private EncoderReader m_leftEncoder;
    private EncoderReader m_righEncoder;
    private GyroReader m_gyro;

    public Drive() {
        loggerCategory = Category.DRIVE;

        m_leftMotor = RobotProvider.instance.getMotor("drive.leftMotor");
        m_rightMotor = RobotProvider.instance.getMotor("drive.rightMotor");
        m_righEncoder = RobotProvider.instance.getEncoder("drive.rightEncoder");
        m_leftEncoder = RobotProvider.instance.getEncoder("drive.leftEncoder");
        m_gyro = RobotProvider.instance.getGyro("drive.gyro");
    }
    public double getEncoderDistance(){
        double leftValue = m_leftEncoder.getDistance();
        double rightValue = m_righEncoder.getDistance();
        log("right and left value is:"+ (rightValue + leftValue) );
        return (leftValue + rightValue)/ 2 ;
    }
    public void setDrivePower(double leftPower, double rightPower) {
        m_leftMotor.set(leftPower);
        m_rightMotor.set(rightPower);
        
	}
	public void setArcadeDrivePower(double forward, double turn) {
		double leftMotorPower = turn + forward;
		double rightMotorPower = -turn + forward;
		setDrivePower(leftMotorPower, rightMotorPower);
    }
    public void resetEncoder(){
        checkContextOwnership();
        m_leftEncoder.reset();
        m_righEncoder.reset();
    }
    public void resetGyro(){
        checkContextOwnership();
        m_gyro.reset();
    }
    public double getGyroAngle(){
        double angle = m_gyro.getAngle();
        log("Gyro:"+angle);
        return(angle);

    }
    


}