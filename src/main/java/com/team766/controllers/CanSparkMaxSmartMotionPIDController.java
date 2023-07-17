wpackage com.team766.controllers;

import com.team766.config.ConfigFileReader;
import com.team766.hal.RobotProvider;
import com.team766.library.SetValueProvider;
import com.team766.library.SettableValueProvider;
import com.team766.library.ValueProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;


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


public class CanSparkMaxSmartMotionPIDController{
	// The attributes of the class include references to the motor controller, SparkMax controller, PID controller, and absolute encoder
	private MotorController mc;
	private CANSparkMax csm;
	private SparkMaxPIDController pid;
	private SparkMaxAbsoluteEncoder abs;
	//PID Related Variables
	private static double dz1 = 0; 
	private static double setPointPosition = 0;
	private static double comboOfTimesInsideDeadzone = 0;
	private static double minPos = 0;
	private static double maxPos = 0;

	private bool isAbsoluteEncoderEnabled;

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
	
	//constructor for the class with no absolute encoder
	public CanSparkMaxSmartMotionPIDController(String configName) throws RuntimeException{
			loggerCategory = Category.MECHANISMS;

			try{
				mc = RobotProvider.instance.getMotor(configName);
				csm = (CANSparkMax)mc;
				pid = csm.getPIDController();
				isAbsoluteEncoderEnabled = true;
			}catch (IllegalArgumentException ill){
				throw new RuntimeException("Error instantiating the PID controller: " + ill);
			}

		
	}
	//constructor for the class with an absolute encoder
	public CanSparkMaxSmartMotionPIDController(String configName, double absEncoderOffset) throws RuntimeException{
			loggerCategory = Category.MECHANISMS;

			try{
				mc = RobotProvider.instance.getMotor(configName);
				csm = (CANSparkMax)mc;
				pid = csm.getPIDController();
				abs = csm.getAbsoluteEncoder(Type.kDutyCycle);
				abs.setZeroOffset(absEncoderOffset);
				pid.setFeedbackDevice(abs);
				isAbsoluteEncoderEnabled = false;
			}catch (IllegalArgumentException ill){
				throw new RuntimeException("Error instantiating the CLE PID controller: " + ill);
			}
			
			
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
	
	//setting the antigravity constants2
	public void setAntigravConstant(double k){
		antiGravK = k;
	}

	private void antigrav(){
		mc.set(antiGravK * Math.sin(mc.getSensorPosition()));
	}

	//adding a built in closed loop error (not tested yet)
	public void setSmartMotionAllowedClosedLoopError(double error){
		pid.setSmartMotionAllowedClosedLoopError(error, 0);
	}
	//changing the deadzone
	public void setDeadzone(double dz){
		dz1 = dz;
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
		maxpos1 = max;
		minpos1 = min;
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

	//go to a position using the thing that wasn't tested yet and almost broke the robot...
	public void setCLEPosition(double position){
		if(position > maxpos1){
			position = maxpos1;
		} else if(position < minpos1){
			position = minpos1;
		}

		pid.setReference(position, ControlType.kSmartMotion);
	}
	//1st step to go to a position using the normal PID, setting what you want the position to be
	public void setSetpoint(double position){
		if(position > maxpos1){
			position = maxpos1;
		} else if(position < minpos1){
			position = minpos1;
		}

		setPointPosition = position;
	}

	public void stop(){
		//Failsafe
		setPointPosition = mc.getSensorPosition();
		theState = PIDSTATE.OFF;
	}

	//run loop that actually runs the PID using normal dz
	//You need to call this function repedatly in OI
	public void run(boolean enabled){
		if(enabled){
			//Checking if Abs encoder is enabled, and if so we wouldn't want positions above 1 and below 0
			if(isAbsoluteEncoderEnabled){
				MathUtil.clamp(minPos, 0.0, 1.0);
				MathUtil.clamp(maxPos, 0.0, 1.0);
			}
			
			switch(theState){
				case OFF:
					break;
				case ANTIGRAV:
					if (mc.getSensorPosition() <= (dz1 + mc.getSensorPosition()) && mc.getSensorPosition() >= (mc.getSensorPosition() - dz1)){
						antigrav();
					} else {
						theState = PIDSTATE.PID;
					}
				case PID:
					if (mc.getSensorPosition() <= (dz1 + mc.getSensorPosition()) && mc.getSensorPosition() >= (mc.getSensorPosition() - dz1)){
						comboOfTimesInsideDeadzone ++;
					} else {
						comboOfTimesInsideDeadzone = 0;
						pid.setReference(setPointPosition, ControlType.kSmartMotion); // todo: testing if this is allowed
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

