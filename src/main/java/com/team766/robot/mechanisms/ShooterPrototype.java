package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.robot.constants.ShooterConstants;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ShooterPrototype extends Mechanism {
    private MotorController leftMotor;
    private MotorController rightMotor;
    private MotorController feederMotor;

    public ShooterPrototype() {
        leftMotor = RobotProvider.instance.getMotor("ShooterPrototype.leftMotor");
        rightMotor = RobotProvider.instance.getMotor("ShooterPrototype.rightMotor");
        feederMotor = RobotProvider.instance.getMotor("ShooterPrototype.feederMotor");
    }

    public void setMotorPower(
            final double leftPower, final double rightPower, final double feederPower) {
        checkContextOwnership();

		SmartDashboard.putNumber("leftPower", leftPower);
		SmartDashboard.putNumber("rightPower", rightPower);
		SmartDashboard.putNumber("feederPower", feederPower);
        leftMotor.set(leftPower < ShooterConstants.CONTROLLER_DEADZONE ? 0 : leftPower);
        rightMotor.set(rightPower < ShooterConstants.CONTROLLER_DEADZONE ? 0 : rightPower);
        feederMotor.set(feederPower < ShooterConstants.CONTROLLER_DEADZONE ? 0 : feederPower);
    }
}
