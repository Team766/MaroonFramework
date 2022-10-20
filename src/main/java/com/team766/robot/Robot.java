package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
	public static Drive drive;
	public static Shooter shooter;
	public static Belts belts;

	public static void robotInit() {
		// Initialize mechanisms here
		drive = new Drive();
		shooter = new Shooter();
		belts = new Belts();
	}
}
