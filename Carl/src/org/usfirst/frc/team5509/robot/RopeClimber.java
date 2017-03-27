package org.usfirst.frc.team5509.robot;

import com.ctre.CANTalon;

public class RopeClimber {
	public CANTalon ropeMotor1, ropeMotor2;

	public double downSpeed = -.5;
	public double holdSpeed = .2;
	public double climbSpeed = 1;

	public void Init() {
		ropeMotor1 = new CANTalon(6);
		ropeMotor2 = new CANTalon(7);
		ropeMotor1.setInverted(true);
	}

	public void move() {
		double joystick2YVal = Robot.joystick2.getRawAxis(1);
		boolean buttonY = Robot.joystick2.getRawButton(4);

		if (((Math.abs(joystick2YVal) > .2) && (buttonY == true))) {
			stop();
		} else if (buttonY == true) {
			ropeMotor1.set(holdSpeed * .88);
			ropeMotor2.set(holdSpeed);
			//Robot.lightsSet(.25);
		} else if (Math.abs(joystick2YVal) > .2) {
			ropeMotor1.set(joystick2YVal * .88);
			ropeMotor2.set(joystick2YVal);
			//Robot.lightsSet(.5);
		} else {
			stop();
		}
	}

	public double applyDeadBand(double value, double deadBand) {
		if (Math.abs(value) < deadBand) {
			return 0;
		}
		return value;
	}

	public void stop() {
		ropeMotor1.set(0);
		ropeMotor2.set(0);
	}
}
