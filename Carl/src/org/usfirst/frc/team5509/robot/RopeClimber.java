package org.usfirst.frc.team5509.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Timer;

public class RopeClimber {
	public CANTalon ropeMotor1, ropeMotor2;
	Timer time = new Timer();

	private double startSpeed = .50;
	public double climbSpeed = 1;
	public double backwardsClimbSpeed = .5;

	public void Init() {
		ropeMotor1 = new CANTalon(6);
		ropeMotor2 = new CANTalon(7);
		ropeMotor2.setInverted(true);
	}

	public void move() {
		if ((((Robot.joystick2.getRawButton(1) == true) && (Robot.joystick2.getRawButton(3) == true))
				|| (((Robot.joystick2.getRawAxis(1) < -.2) && (Robot.joystick2.getRawButton(3) == true)))
				|| (((Robot.joystick2.getRawButton(1) == true) && (Robot.joystick2.getRawAxis(1) < -.2))))) {
			stop();
		} else if (Robot.joystick2.getRawButton(1) == true) {
			if (time.get() == 0) {
				time.start();
			}
			ropeMotor1.set(getTime() * .88);
			ropeMotor2.set(getTime());
		} else if (Robot.joystick2.getRawButton(2) == true) {
			ropeMotor1.set(-backwardsClimbSpeed * .88);
			ropeMotor2.set(-backwardsClimbSpeed);
			if (time.get() != 0) {
				time.stop();
				time.reset();
			}
		} else if (Robot.joystick2.getRawAxis(1) < -.2) {
			ropeMotor1.set(-Robot.joystick2.getRawAxis(1) * .88);
			ropeMotor2.set(-Robot.joystick2.getRawAxis(1));
		} else if ((((Robot.joystick2.getRawButton(1) == false) && (Robot.joystick2.getRawButton(3) == false))
				|| (((Robot.joystick2.getRawAxis(1) > -.2) && (Robot.joystick2.getRawButton(3) == false)))
				|| (((Robot.joystick2.getRawButton(1) == false) && (Robot.joystick2.getRawAxis(1) > -.2))))) {
			stop();
		}
	}

	private double getTime() {
		if (time.get() == 0) {
			return climbSpeed;
		}

		System.out.println(time.get() + " = time");

		if (time.get() <= .5) {
			return startSpeed;
		} else if (time.get() <= 1) {
			return time.get();
		} else {
			return climbSpeed;
		}
	}

	public void stop() {
		time.stop();
		time.reset();
		ropeMotor1.set(0);
		ropeMotor2.set(0);
	}
}
