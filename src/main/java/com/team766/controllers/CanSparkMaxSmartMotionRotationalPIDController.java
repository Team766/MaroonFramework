package com.team766.controllers;

import com.team766.config.ConfigFileReader;
import com.team766.hal.RobotProvider;
import com.team766.library.SetValueProvider;
import com.team766.library.SettableValueProvider;
import com.team766.library.ValueProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import edu.wpi.first.math.MathUtil;
import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import java.io.IOError;
import javax.swing.text.DefaultStyledDocument.ElementSpec;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.library.RateLimiter;


public class CanSparkMaxSmartMotionRotationalPIDController{
	
	// The attributes of the class include references to the motor controller, SparkMax controller, PID controller, and absolute encoder
	private MotorController mc;
	private CANSparkMax csm;
	private SparkMaxPIDController pid;
	private SparkMaxAbsoluteEncoder abs;
	
	//PID Related Variables
	private static double deadzone = 0; 
	private static double setPointPosition = 0;
	private static double comboOfTimesInsideDeadzone = 0;
	private static double minPos = 0;
	private static double maxPos = 0;


	//Precision variables
	private double degreesToEncoderUnitsRatio = 0;
	private double encoderUnitsPerOneAbsolute = 0;

	//antigrav coefficient
	private static double antiGravK;
	//enum for which state the PID is in
	public enum PIDSTATE{
		PID,
		OFF,
		ANTIGRAV
	}
	//the state of the PID
	private PIDSTATE theState = PIDSTATE.OFF;
	
	//constructor for the class not using an absolute encoder for kDutyCycle
	
	public CanSparkMaxSmartMotionRotationalPIDController(String configName, double absEncoderOffset, double absEncoderOffsetForZeroEncoderUnits, OffsetPoint first, OffsetPoint second, double ratio){
			loggerCategory = Category.MECHANISMS;

			try{
				mc = RobotProvider.instance.getMotor(configName);
				csm = (CANSparkMax)mc;
				pid = csm.getPIDController();
				abs = csm.getAbsoluteEncoder(Type.kDutyCycle);
				abs.setZeroOffset(absEncoderOffset);
				degreesToEncoderUnitsRatio = ratio;


				double absoluteDifference = second.getAbsoluteValue() - first.getAbsoluteValue();
				double motorEncoderDiference = second.getMotorEncoderValue() - first.getMotorEncoderValue();

				encoderUnitsPerOneAbsolute = motorEncoderDiference/absoluteDifference;

				double relEncoder = absToEu(abs.getPosition() - absEncoderOffsetForZeroEncoderUnits);

				mc.setSensorPosition(relEncoder);
				

			}catch (IllegalArgumentException ill){
				throw new RuntimeException("Error instantiating the PID controller: " + ill);
			}

		
	}

	private double absToEu(double abs){
		return encoderUnitsPerOneAbsolute / (1/abs); 
	}

	private double euToDegrees(double eu){
		return eu * degreesToEncoderUnitsRatio;
	}

	private double degreesToEu(double degrees){
		return (1/degreesToEncoderUnitsRatio) * degrees;
	}

	//manually changing the state
	public void updateState(PIDSTATE state){
		theState = state;
	}
	//changing all PID values at once
	public void setPIDF(double p, double i, double d, double ff){
		pid.setP(p);
		pid.setI(i);
		pid.setD(d);
		pid.setFF(ff);
	}
	//changing the P value
	public void setP(double p){
		pid.setP(p);
	}
	//changing the I value
	public void setI(double i){
		pid.setI(i);
	}
	//changing the D value
	public void setD(double d){
		pid.setD(d);
	}
	//changing the FF value
	public void setFf(double ff){
		pid.setFF(ff);
	}
	
	/*
	* Here we set the antigrav constant
 	* If the mechanism is rotational, this is the amount we multiply the Sine of the sensor position with
  	* If the mechanism isn't rotational, this is just the amount of power to apply.
   	* @param k the value to set according to the above condition
	*/
	public void setAntigravConstant(double k){
		antiGravK = k;
	}

	private void antigrav(){
		mc.set(antiGravK * Math.sin(euToDegrees(mc.getSensorPosition())));
	}

	//changing the deadzone
	public void setDeadzone(double dz){
		deadzone = dz;
	}
	//changing the output range of the speed of the motors
	public void setOutputRange(double min, double max){
		pid.setOutputRange(min, max);
	}
	//changing the neutral mode of the motor (brake/coast)
	public void setMotorMode(NeutralMode mode){
		mc.setNeutralMode(mode);
	}
	//setting the maximum and minimum locations that the motor can go to
	public void setMinMaxLocation(double min, double max){
		maxPos = max;
		minPos = min;
	}
	//setting the maximum velocity of the motor
	public void setMaxVel(double max){
		pid.setSmartMotionMaxVelocity(max, 0);
		pid.setSmartMotionMinOutputVelocity(0, 0);
	}
	//setting the maximum acceleration of the motor
	public void setMaxAccel(double max){
		pid.setSmartMotionMaxAccel(max, 0);
	}

	//1st step to go to a position using the normal PID, setting what you want the position to be
	public void setSetpoint(double positionInDegrees){
		setPointPosition = MathUtil.clamp(degreesToEu(positionInDegrees), minPos, maxPos);
		theState = PIDSTATE.PID;
	}

	//Failsafe
	public void stop(){
		setPointPosition = mc.getSensorPosition();
		theState = PIDSTATE.OFF;
	}

	//run loop that actually runs the PID 
	//You need to call this function repedatly in OI as often as possible
	public void run(boolean enabled){
		if(enabled){
			//Checking if Abs encoder is enabled, and if so we wouldn't want positions above 1 and below 0
			
			switch(theState){
				case OFF:
					mc.set(0);
					break;
				case ANTIGRAV:
					if (setPointPosition <= (deadzone + mc.getSensorPosition()) && setPointPosition >= (mc.getSensorPosition() - deadzone)){
						antigrav();
					} else {
						theState = PIDSTATE.PID;
					}
				case PID:
					if (setPointPosition <= (deadzone + mc.getSensorPosition()) && setPointPosition >= (mc.getSensorPosition() - deadzone)){
						comboOfTimesInsideDeadzone ++;
					} else {
						comboOfTimesInsideDeadzone = 0;
						pid.setReference(setPointPosition, ControlType.kSmartMotion); 
					}
		
					if(comboOfTimesInsideDeadzone >= 6){
						theState = PIDSTATE.ANTIGRAV;
					}
					break;
			}
		} else{
			log("enabled is false on run loop PID controller");
		}
		
	}

	}


