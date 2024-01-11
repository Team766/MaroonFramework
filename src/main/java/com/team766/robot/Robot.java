package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
    // Declare mechanisms here
    public static ShooterPrototype shooterPrototype;

    public static void robotInit() {
        // Initialize mechanisms here
        shooterPrototype = new ShooterPrototype();
    }
}
