package com.team766.simulator;

import java.lang.reflect.Array;
import com.team766.hal.mock.MockJoystick;

public class ProgramInterface {
	public static Runnable programStep = () -> {};
	
	public static double simulationTime;

	public static int driverStationUpdateNumber = 0;

	public static enum RobotMode {
		DISABLED, AUTON, TELEOP
	}

	public static RobotMode robotMode = Parameters.INITIAL_ROBOT_MODE;
	
	public static final double[] pwmChannels = new double[20];
	
	public static class CANSpeedControllerCommand {
		public enum ControlMode {
			PercentOutput,
			Position,
			Velocity,
			Current,
			Follower,
			MotionProfile,
			MotionMagic,
			MotionProfileArc,
			Voltage,
			Disabled,
		}
		
		public double output;
		public ControlMode controlMode;
	}
	public static class CANSpeedControllerStatus {
		public double sensorPosition;
		public double sensorVelocity;
	}
	public static class CANSpeedControllerCommunication {
		public final CANSpeedControllerCommand command = new CANSpeedControllerCommand();
		public final CANSpeedControllerStatus status = new CANSpeedControllerStatus();
	}
	
	public static final CANSpeedControllerCommunication[] canSpeedControllerChannels =
			initializeArray(256, CANSpeedControllerCommunication.class);
	
	public static final double[] analogChannels = new double[20];
	
	public static final boolean[] digitalChannels = new boolean[20];
	
	public static final int[] relayChannels = new int[20];
	
	public static final boolean[] solenoidChannels = new boolean[20];
	
	public static class EncoderChannel {
		public long distance = 0;
		public double rate = 0;
	}

	public static final EncoderChannel[] encoderChannels =
		initializeArray(20, EncoderChannel.class);
	
	public static class GyroCommunication {
		public double angle; // Yaw angle (accumulative)
		public double rate;  // Yaw rate
		public double pitch;
		public double roll;
	}
	
	public static final GyroCommunication gyro = new GyroCommunication();

	public static class RobotPosition {
		public double x;
		public double y;
		public double heading;
	}

	public static final RobotPosition robotPosition = new RobotPosition();
	
	public static final MockJoystick[] joystickChannels =
			initializeArray(4, MockJoystick.class);
	
	
	private static <E> E[] initializeArray(int size, Class<E> clazz) {
		@SuppressWarnings("unchecked")
		E[] array = (E[]) Array.newInstance(clazz, size);
		for (int i = 0; i < size; ++i) {
			try {
				array[i] = clazz.getConstructor().newInstance();
			} catch (Throwable e) {
				throw new ExceptionInInitializerError(e);
			}
		}
		return array;
	}
}
