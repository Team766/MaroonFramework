package com.team766.controllers;

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
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.library.RateLimiter;

public class PID {
	private MotorController mc1; 
    private CANSparkMax csm1;
    private SparkMaxPIDController pid1;
    private SparkMaxAbsoluteEncoder abs1;
	public static double dz1; 
	public static double maxpos1;
	public static double minpos1;
	public static double maxvel1;
	public static double maxaccel1;
	public static double maxspeed1;
	public static double minspeed1;

	private MotorController mc2; 
    private CANSparkMax csm2; 
    private SparkMaxPIDController pid2;
    private SparkMaxAbsoluteEncoder abs2;
	public static double dz2;
	public static double maxpos2;
	public static double minpos2;
	public static double maxvel2;
	public static double maxaccel2;
	public static double maxspeed2;
	public static double minspeed2;

	private MotorController mc3;
    private CANSparkMax csm3;
    private SparkMaxPIDController pid3;
    private SparkMaxAbsoluteEncoder abs3;
	public static double dz3;
	public static double maxpos3;
	public static double minpos3;
	public static double maxvel3;
	public static double maxaccel3;
	public static double maxspeed3;
	public static double minspeed3;

	private MotorController mc4;
    private CANSparkMax csm4;
    private SparkMaxPIDController pid4;
    private SparkMaxAbsoluteEncoder abs4;
	public static double dz4;
	public static double maxpos4;
	public static double minpos4;
	public static double maxvel4;
	public static double maxaccel4;
	public static double maxspeed4;
	public static double minspeed4;

	private MotorController mc5;
    private CANSparkMax csm5; 
    private SparkMaxPIDController pid5;  
    private SparkMaxAbsoluteEncoder abs5; 
	public static double dz5;
	public static double maxpos5;
	public static double minpos5;
	public static double maxvel5;
	public static double maxaccel5;
	public static double maxspeed5;
	public static double minspeed5;


	public PID(int numMotors){
		if(numMotors >5){
			Logger.get(Category.CONTROL).log(Severity.ERROR, "PID: Too many motors. Please add more manually to PID.java");
		} else if(numMotors < 1){
			Logger.get(Category.CONTROL).log(Severity.ERROR, "PID: how u gonna make a pid with no motors just stop");
		} else if(numMotors > 4){
			mc5 = RobotProvider.instance.getMotor("pid5");
			csm5 = (CANSparkMax)mc5;
			pid5  = csm5.getPIDController();
		} else if(numMotors > 3){
			mc5 = RobotProvider.instance.getMotor("pid5");
			csm5 = (CANSparkMax)mc5;
			pid5  = csm5.getPIDController();
			mc4 = RobotProvider.instance.getMotor("pid4");
			csm4 = (CANSparkMax)mc4;
			pid4  = csm4.getPIDController();
		} else if(numMotors > 2){
			mc5 = RobotProvider.instance.getMotor("pid5");
			csm5 = (CANSparkMax)mc5;
			pid5  = csm5.getPIDController();
			mc4 = RobotProvider.instance.getMotor("pid4");
			csm4 = (CANSparkMax)mc4;
			pid4  = csm4.getPIDController();
			mc3 = RobotProvider.instance.getMotor("pid3");
			csm3 = (CANSparkMax)mc3;
			pid3  = csm3.getPIDController();
		} else if(numMotors > 1){
			mc5 = RobotProvider.instance.getMotor("pid5");
			csm5 = (CANSparkMax)mc5;
			pid5  = csm5.getPIDController();
			mc4 = RobotProvider.instance.getMotor("pid4");
			csm4 = (CANSparkMax)mc4;
			pid4  = csm4.getPIDController();
			mc3 = RobotProvider.instance.getMotor("pid3");
			csm3 = (CANSparkMax)mc3;
			pid3  = csm3.getPIDController();
			mc2 = RobotProvider.instance.getMotor("pid2");
			csm2 = (CANSparkMax)mc2;
			pid2 = csm2.getPIDController();
		} else if(numMotors > 0){
			mc5 = RobotProvider.instance.getMotor("pid5");
			csm5 = (CANSparkMax)mc5;
			pid5  = csm5.getPIDController();
			mc4 = RobotProvider.instance.getMotor("pid4");
			csm4 = (CANSparkMax)mc4;
			pid4  = csm4.getPIDController();
			mc3 = RobotProvider.instance.getMotor("pid3");
			csm3 = (CANSparkMax)mc3;
			pid3  = csm3.getPIDController();
			mc2 = RobotProvider.instance.getMotor("pid2");
			csm2 = (CANSparkMax)mc2;
			pid2 = csm2.getPIDController();
			mc1 = RobotProvider.instance.getMotor("pid1");
			csm1 = (CANSparkMax)mc1;
			pid1 = csm1.getPIDController();
		}
	}
}
/* 
} else if(numMotors > 4){
	mc5.setNeutralMode(NeutralMode.Brake);
	mc5.setInverted(true);
	pid5.setP(0.0001);
	pid5.setI(0);
	pid5.setD(0);
	pid5.setIZone(0);
	pid5.setFF(0);
	pid5.setOutputRange(min5, max5);
	abs5.setPosition(0);
} else if(numMotors > 3){
	mc4.setNeutralMode(NeutralMode.Brake);
	mc4.setInverted(true);
	pid4.setP(0.0001);
	pid4.setI(0);
	pid4.setD(0);
	pid4.setIZone(0);
	pid4.setFF(0);
	pid4.setOutputRange(min4, max4);
	abs4.setPosition(0);
} else if(numMotors > 2){
	mc3.setNeutralMode(NeutralMode.Brake);
	mc3.setInverted(true);
	pid3.setP(0.0001);
	pid3.setI(0);
	pid3.setD(0);
	pid3.setIZone(0);
	pid3.setFF(0);
	pid3.setOutputRange(min3, max3);
	abs3.setPosition(0);
} else if(numMotors > 1){
	mc2.setNeutralMode(NeutralMode.Brake);
	mc2.setInverted(true);
	pid2.setP(0.0001);
	pid2.setI(0);
	pid2.setD(0);
	pid2.setIZone(0);
	pid2.setFF(0);
	pid2.setOutputRange(min2, max2);
	abs2.setPosition(0);
} else if(numMotors > 0){
	mc1.setNeutralMode(NeutralMode.Brake);
	mc1.setInverted(true);
	pid1.setP(0.0001);
	pid1.setI(0);
	pid1.setD(0);
	pid1.setIZone(0);
	pid1.setFF(0);
	pid1.setOutputRange(min1, max1);
	abs1.setPosition(0);
}


*/