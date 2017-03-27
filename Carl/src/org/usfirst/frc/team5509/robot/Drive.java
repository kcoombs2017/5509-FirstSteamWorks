package org.usfirst.frc.team5509.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.RobotDrive;

public class Drive {

	public CANTalon backRightMotor, frontRightMotor, frontLeftMotor, backLeftMotor;
	public double limitPercent = .85;

	RobotDrive robotDrive;

	public void Init() {
		backRightMotor = new CANTalon(1);
		frontRightMotor = new CANTalon(2);
		frontLeftMotor = new CANTalon(3);
		backLeftMotor = new CANTalon(4);

		backRightMotor.setInverted(true);
		frontRightMotor.setInverted(true);
		backLeftMotor.setInverted(false);
		frontLeftMotor.setInverted(false);

		robotDrive = new RobotDrive(backLeftMotor, frontLeftMotor, backRightMotor, frontRightMotor);
		robotDrive.setSafetyEnabled(false);
	}

	public void move() {
		if (backLeftMotor.getEncPosition() < -170) {
			backLeftMotor.setEncPosition(0);
		}

		double x = Robot.joystick1.getRawAxis(0);
		double y = Robot.joystick1.getRawAxis(1);
		double rotation = Robot.joystick1.getRawAxis(4);

		x = applyDeadBand(x, .2);
		y = applyDeadBand(y, .2);
		rotation = applyDeadBand(rotation, .2);
		
		robotDrive.mecanumDrive_Cartesian(-x * limitPercent, y * limitPercent, rotation * limitPercent, 0);
	}

	public double applyDeadBand(double value, double deadBand) {
		if (Math.abs(value) < deadBand) {
			return 0;
		}
		return value;
	}

	public void stop() {
		robotDrive.stopMotor();
	}

}
