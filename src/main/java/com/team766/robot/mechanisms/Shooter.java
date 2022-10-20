package com.team766.robot.mechanisms;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController.ControlMode;
import com.team766.logging.Category;

public class Shooter extends Mechanism{
	private MotorController shooter;

	public Shooter() {
		shooter = RobotProvider.instance.getMotor("shooter");
		shooter.restoreFactoryDefault();
		shooter.setClosedLoopRamp(1.5);
		shooter.setOpenLoopRamp(1.5);
		shooter.setCurrentLimit(30);
		loggerCategory = Category.PROCEDURES;
		setPIDValues();
	}

	public void setVelocity(double power){
		checkContextOwnership();
		log("Power is: "+power);
		shooter.set(ControlMode.Velocity, power);
	}

	public void basicShoot(){
		checkContextOwnership();
		setPIDValues();
		double power = ConfigFileReader.getInstance().getDouble("shooter.velocity").valueOr(1.0);
		setVelocity(power);
	}
	

	public void stopShoot(){
		checkContextOwnership();
		shooter.set(0.0);
	}

	public void setPIDValues(){
		shooter.setP(ConfigFileReader.getInstance().getDouble("shooter.p").valueOr(0.0));
		shooter.setI(ConfigFileReader.getInstance().getDouble("shooter.i").valueOr(0.0));
		shooter.setD(ConfigFileReader.getInstance().getDouble("shooter.d").valueOr(0.0));
		shooter.setFF(ConfigFileReader.getInstance().getDouble("shooter.ff").valueOr(0.0)); //overcome friction
	}

	public double getVelocity(){
		return shooter.getSensorVelocity();
	}
}
