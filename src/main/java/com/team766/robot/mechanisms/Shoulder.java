package com.team766.robot.mechanisms;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import com.revrobotics.SparkMaxPIDController;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Shoulder extends Mechanism{


	private double kF;
	private double kP;
	private double kI; // will most likley be 0
	private double kD;

	private double setpoint;
	private double allowedError;

	private double maxSpeed;
	private double minSpeed;
	private double maxAccel;
	private double maxVel;

	public MotorController shoulderHallMotor = RobotProvider.instance.getMotor("shoulder"); // public for changing brake/coast mode without using a method
	private CANSparkMax shoulderCanSparkMax = (CANSparkMax) shoulderHallMotor;
	private SparkMaxPIDController shoulderPIDController = shoulderCanSparkMax.getPIDController();

	public MotorController shoulderHallMotor2 = RobotProvider.instance.getMotor("shoulder2"); // public for changing brake/coast mode without using a method
	private CANSparkMax shoulderCanSparkMax2 = (CANSparkMax) shoulderHallMotor2;


	private static final double MAX_POSITION = 0; // figure it out
	private static final double MIN_POSITION = 0; // figure it out

	private static final double conePosition = 0; // figure it out
	private static final double cubePosition = 0; // figure it out
	private static final double restingPosition = 0; // figure it out


	public Shoulder() {
		kF = ConfigFileReader.getInstance().getDouble("shoulder.kF").get();
		kP = ConfigFileReader.getInstance().getDouble("shoulder.kP").get();
		kI = ConfigFileReader.getInstance().getDouble("shoulder.kI").get();
		kD = ConfigFileReader.getInstance().getDouble("shoulder.kD").get();

		allowedError = ConfigFileReader.getInstance().getDouble("shoulder.allowedError").get();

		maxSpeed = ConfigFileReader.getInstance().getDouble("shoulder.maxSpeed").get();
		minSpeed = ConfigFileReader.getInstance().getDouble("shoulder.minSpeed").get();
		maxAccel = ConfigFileReader.getInstance().getDouble("shoulder.maxAccel").get();
		maxVel = ConfigFileReader.getInstance().getDouble("shoulder.maxVel").get();

		shoulderPIDController.setP(kP);
		shoulderPIDController.setI(kI);
		shoulderPIDController.setD(kD);
		shoulderPIDController.setFF(kF);
		shoulderPIDController.setOutputRange(minSpeed, maxSpeed);
		shoulderPIDController.setSmartMotionMaxVelocity(maxVel, 0);
		shoulderPIDController.setSmartMotionMaxAccel(maxAccel, 0);
		shoulderPIDController.setSmartMotionMinOutputVelocity(0, 0);


		shoulderPIDController.setSmartMotionAllowedClosedLoopError(allowedError, 0);
		shoulderCanSparkMax1.getEncoder().setPosition(0); //reset to 0 at beginning.
		shoulderCanSparkMax2.getEncoder().setPosition(0); //reset to 0 at beginning.
		shoulderCanSparkMax2.follow(shoulderCanSparkMax1, true);
	}


	public void setPosition(final double position){
		setpoint = position;
	}

	//should be called AOAP
	public void run(){
		shoulderPIDController.setReference(setpoint, com.revrobotics.ControlType.kPosition);
	}
	
}
