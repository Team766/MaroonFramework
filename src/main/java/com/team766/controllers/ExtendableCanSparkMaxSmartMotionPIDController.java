package com.team766.controllers;

import com.team766.config.ConfigFileReader;
import com.team766.hal.RobotProvider;
import com.team766.library.SetValueProvider;
import com.team766.library.SettableValueProvider;
import com.team766.library.ValueProvider;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
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
import com.team766.controllers.pidstate.*;
import com.team766.framework.Exceptions.*;


public abstract class ExtendableCanSparkMaxSmartMotionPIDController {
		/*
		 * These are the hardware variables for the hardware we have on the robot.
		 * It includes a CanSparkMax, MotorController of any type, and a SparkMaxAbsoluteEncoder.
		 * @author Max Spier - 9/9/2023
		 */
		private MotorController mc;
		private CANSparkMax csm;
		private SparkMaxAbsoluteEncoder abs;
	
		/*
		 * These are PID related variables that we will store locally to run PIDs
		 * We also create an object for the PIDController
		 * @author Max Spier - 9/9/2023
		 */
		private SparkMaxPIDController pid;
		private static double deadzone = 0;
		private static double setPointPosition = 0;
		private static double comboOfTimesInsideDeadzone = 0;
		private static double minPos = 0;
		private static double maxPos = 0;
	
		/*
		 * These are some variables that we can use so that we can reset the motor controller
		 * We use this so that we can get an accurate ratio for the hall encoder units to absolute encoder units.
		 * @author Max Spier - 9/9/2023
		 */
		private double encoderUnitsPerOneAbsolute = 0;

		private double curAntigrav = 0;
	
	
		/*
		* The state of the PID controller so that we know whether to call the respective method for each state.
		* For more documentation, see the PIDSTATE class.
		* @author Max Spier - 9/9/2023
		*/
		
		private PIDSTATE theState = PIDSTATE.OFF;

	/*
	 * Default constructor for the class. This is used to create a new CanSparkMaxSmartMotionPIDController object
	 * @param configName the configuration name for the motorcontroller
	 * @param absEncoderOffset the offset (ranging from 0.000 - 0.999) of the absolute encoder
	 * @param absEncoderOffsetForZeroEncoderUnits the offset that the absolute encoder should have in order for the relative encoder on the motorcontroller to equal zero.
	 * @param first the first offsetpoint used to reset the encoders - for more info see the OffsetPoint class
	 * @param second the second offsetpoint used to reset the encoders - for more info see the OffsetPoint class
	 * @author Max Spier - 9/9/2023
	 */

	
	public ExtendableCanSparkMaxSmartMotionPIDController(final String configName, final double absEncoderOffset, final double absEncoderOffsetForZeroEncoderUnits, final OffsetPoint first, final OffsetPoint second) {
		try {
			mc = RobotProvider.instance.getMotor(configName);
			csm = (CANSparkMax) mc;
			pid = csm.getPIDController();
			abs = csm.getAbsoluteEncoder(Type.kDutyCycle);
			abs.setZeroOffset(absEncoderOffset);

			double absoluteDifference = second.getAbsoluteValue() - first.getAbsoluteValue();
			double motorEncoderDiference = second.getMotorEncoderValue() - first.getMotorEncoderValue();

			encoderUnitsPerOneAbsolute = motorEncoderDiference / absoluteDifference;

			double relEncoder = absToEu(abs.getPosition() - absEncoderOffsetForZeroEncoderUnits);

			mc.setSensorPosition(relEncoder);

		} catch (IllegalArgumentException ill) {
			throw new AbstractPIDRuntimeException("Error instantiating the PID controller: " + ill);
		}
	}

	/*
	 * Method to convert the absolute encoder units into hall encoder units
	 * @author Max Spier - 9/9/2023
	 */
	private double absToEu(final double abs) {
		return encoderUnitsPerOneAbsolute * abs;
	}
	
	/*
	 * Method to change all PIDF values at once
	 * @params p-ff the values to set each one for the PIDController
	 * @author Max Spier - 9/9/2023
	 */
	public void setPIDF(final double p, final double i, final double d, final double ff) {
		pid.setP(p);
		pid.setI(i);
		pid.setD(d);
		pid.setFF(ff);
	}

	/*
	 * Method to change the P value of the PIDController
	 * @param p the value of P to set for the PIDController
	 * @author Max Spier - 9/9/2023
	 */
	public void setP(final double p) {
		pid.setP(p);
	}

	/*
	 * Method to change the I value of the PIDController
	 * @param i the value of I to set for the PIDController
	 * @author Max Spier - 9/9/2023
	 */
	public void setI(final double i) {
		pid.setI(i);
	}

	/*
	 * Method to change the D value of the PIDController
	 * @param d the value of D to set for the PIDController
	 * @author Max Spier - 9/9/2023
	 */
	public void setD(final double d) {
		pid.setD(d);
	}

	/*
	 * Method to change the FF value of the PIDController
	 * @param ff the value of FF to set for the PIDController
	 * @author Max Spier - 9/9/2023
	 */
	public void setFF(final double ff) {
		pid.setFF(ff);
	}

	/*
	 * Method to update the value of the deadzone for the PIDController
	 * This deadzone is in hall encoder units.
	 * @param dz the value of the deadzone in hall encoder units
	 * @author Max Spier - 9/9/2023
	 */
	public void setDeadzone(final double dz) {
		deadzone = dz;
	}

	/*
	 * This is a method to update the minimum and maximum output ranges of motorpower for the mechanism
	 * @param min the minimum value for motor power (make sure that it is negative if you want the mechanism to be able to go both directions)
	 * @param max the maximum value for motor power (make sure that it is positive if you want the mechanism to be able to go both directions)
	 * @author Max Spier - 9/9/2023
	 */
	public void setOutputRange(final double min, final double max) {
		pid.setOutputRange(min, max);
	}

	/*
	 * This is the method to set the minimum and maximum locations of the mechanism.
	 * Please check your subclasses documentation for what units this is supposed to be in (usually whichever one is used as an input for the setSetpoint method)
	 * @author Max Spier - 9/9/2023
	 */
	public void setMinMaxLocation(final double min, final double max) {
		maxPos = max;
		minPos = min;
	}

	/*
	* This method is used to set the maximum velocity of the mechanism for use with PID
	* @param max the value of the maximum amount of velocity for the motorcontroller
	* The method also sets the minimum velocity to zero, as a failsafe.
	* @author Max Spier - 9/9/2023
	*/
	public void setMaxVelocity(final double max) {
		pid.setSmartMotionMaxVelocity(max, 0);
		pid.setSmartMotionMinOutputVelocity(0, 0);
	}

	/*
	 * This is the method to set the maximum acelleration of the robot for the PIDController
	 * @param max the value for the maximum acelleration
	 * @author Max Spier - 9/9/2023
	 */
	public void setMaxAccel(final double max) {
		pid.setSmartMotionMaxAccel(max, 0);
	}

	/*
	 * This is the method where you update the setpoint position from the subclass
	 * @param x the setpoint
	 * @author Max Spier - 9/9/2023
	 */
	protected void updateSetpointFromSubclass(double x){
		x = MathUtil.Clamp(x, minPos, maxPos);
		setPointPosition = x;
	}

	/*
	 * Failsafe for the PIDController to stop it.
	 * @author Max Spier - 9/9/2023
	 */
	public void stop() {
		theState = PIDSTATE.OFF;
	}

	/*
	 * This method does the antigrav
	 * @author Max Spier - 9/9/2023
	 */
	private void antigrav(){
		mc.set(curAntigrav);
	}

	/*
	 * This method sets the antigrav power amount.
	 * It gets the value for this from the subclass
	 * @param passedValueFromSubclass the power to set the motorcontroller for antigrav
	 * @author Max Spier - 9/9/2023
	 */
	protected void updateAntigrav(double passedValueFromSubclass){
		curAntigrav = passedValueFromSubclass
	}

	/*
	 * This is the run loop that actually runs the PID Controller
	 * You need to call this as frequently as possible when running the mechanism
	 * @author Max Spier - 9/9/2023
	 */
	public void run() {
		switch (theState) {
			case OFF:
				mc.set(0);
				break;
			case ANTIGRAV:
				if (setPointPosition <= (deadzone + mc.getSensorPosition()) && setPointPosition >= (mc.getSensorPosition() - deadzone)) {
					antigrav();
				} else {
					theState = PIDSTATE.PID;
				}
			case PID:
				if (setPointPosition <= (deadzone + mc.getSensorPosition()) && setPointPosition >= (mc.getSensorPosition() - deadzone)) {
					comboOfTimesInsideDeadzone++;
				} else {
					comboOfTimesInsideDeadzone = 0;
					pid.setReference(setPointPosition, ControlType.kSmartMotion);
				}

				if (comboOfTimesInsideDeadzone >= 6) {
					theState = PIDSTATE.ANTIGRAV;
				}
				break;
			default:
				throw new AbstractPIDRuntimeException("Invalid state error");
				break;
		}
	}
	







}
