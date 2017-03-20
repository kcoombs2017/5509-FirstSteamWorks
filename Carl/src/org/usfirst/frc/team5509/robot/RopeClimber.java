package org.usfirst.frc.team5509.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Timer;

public class RopeClimber {
	public CANTalon ropeMotor1, ropeMotor2;

	public double downSpeed = -.5;
	public double holdSpeed = .2;
	public double backwardsClimbSpeed = 1;

	public void Init() {
		ropeMotor1 = new CANTalon(6);
		ropeMotor2 = new CANTalon(7);
		ropeMotor1.setInverted(true);
	}

	public void move() {
		if ((((Robot.joystick2.getRawAxis(1) > .2) && (Robot.joystick2.getRawButton(2) == true)))
				|| ((Robot.joystick2.getRawButton(1) == true) && (Robot.joystick2.getRawAxis(1) > .2))) {
			stop();
		} else if (Robot.joystick2.getRawButton(2) == true) {
			ropeMotor1.set(downSpeed * .88);
			ropeMotor2.set(downSpeed);
		} else if (Robot.joystick2.getRawButton(4) == true) {
			ropeMotor1.set(holdSpeed * .88);
			ropeMotor2.set(holdSpeed);
		} else if (Robot.joystick2.getRawButton(1) == true) {
			ropeMotor1.set(backwardsClimbSpeed * .88);
			ropeMotor2.set(backwardsClimbSpeed);
		} else if (Robot.joystick2.getRawAxis(1) > .2) {
			ropeMotor1.set(Robot.joystick2.getRawAxis(1) * .88);
			ropeMotor2.set(Robot.joystick2.getRawAxis(1));
		} else if (Robot.joystick2.getRawAxis(1) < -.2) {
			ropeMotor1.set(-Robot.joystick2.getRawAxis(1) * .88);
			ropeMotor2.set(-Robot.joystick2.getRawAxis(1));
		} else if ((((Robot.joystick2.getRawButton(1) == false) && (Robot.joystick2.getRawButton(3) == false))
				|| (((Robot.joystick2.getRawAxis(1) > -.2) && (Robot.joystick2.getRawButton(3) == false)))
				|| (((Robot.joystick2.getRawButton(1) == false) && (Robot.joystick2.getRawAxis(1) > -.2))))) {
			stop();
		}
	}

	public void stop() {
		ropeMotor1.set(0);
		ropeMotor2.set(0);
	}
}
