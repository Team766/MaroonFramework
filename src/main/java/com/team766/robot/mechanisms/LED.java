package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController;
import com.ctre.phoenix.led.*;

public class LED extends Mechanism {
	private ColorFlowAnimation color1;
	private ColorFlowAnimation color2;
	private RainbowAnimation rainbowAnim;
	private CANdle candle;
	private final int deviceID = 5;

	public LED() {
		candle = new CANdle(deviceID);
		color1 = new ColorFlowAnimation(0,100,0);
		color2 = new ColorFlowAnimation(0,0,100);
		rainbowAnim = new RainbowAnimation(1, 0.5, 64);
	}

	public void animate1(){
		candle.animate(color1);
		candle.animate(color2);
		// This runs only the bottom thing
	}

	public void rainbowanimate(){
		
		candle.animate(rainbowAnim);
	}

	public void reset(){
		candle.setLEDs(255,255,255);
	}
}