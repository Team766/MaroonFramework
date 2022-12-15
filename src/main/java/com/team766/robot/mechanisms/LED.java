package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController;
import com.ctre.phoenix.led.*;

public class LED extends Mechanism {
	private ColorFlowAnimation color1;
	private ColorFlowAnimation color2;
	private CANdle candle;
	private final int deviceID = 5;

	public LED() {
		candle = new CANdle(deviceID);
		color1 = new ColorFlowAnimation(0,100,0);
		color2 = new ColorFlowAnimation(0,0,100);
	}

	public void animate(){
		candle.animate(color1);
		candle.animate(color2);
	}

	public void reset(){
		candle.setLEDs(255,255,255);
	}
}