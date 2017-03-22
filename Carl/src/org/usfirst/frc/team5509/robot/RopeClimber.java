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
		double x = Robot.joystick2.getRawAxis(1);
		if (((Math.abs(x) > .2) && (Robot.joystick2.getRawButton(2) == true))
				|| ((Robot.joystick2.getRawButton(1) == true) && (Robot.joystick2.getRawAxis(1) > .2))) {
			stop();
		} else if (Robot.joystick2.getRawButton(2) == true) {
			ropeMotor1.set(downSpeed * .88);
			ropeMotor2.set(downSpeed);
			Robot.lightsSet(1);
		} else if (Robot.joystick2.getRawButton(4) == true) {
			ropeMotor1.set(holdSpeed * .88);
			ropeMotor2.set(holdSpeed);
			Robot.lightsSet(.25);
		} else if (Robot.joystick2.getRawButton(1) == true) {
			ropeMotor1.set(climbSpeed * .88);
			ropeMotor2.set(climbSpeed);
			Robot.lightsSet(.5);
		} else if (Math.abs(x) > .2) {
			ropeMotor1.set(x * .88);
			ropeMotor2.set(x);
			Robot.lightsSet(.5);
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
