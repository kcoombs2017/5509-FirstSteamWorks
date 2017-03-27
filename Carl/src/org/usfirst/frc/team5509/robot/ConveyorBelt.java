package org.usfirst.frc.team5509.robot;

import com.ctre.CANTalon;

public class ConveyorBelt {
	public CANTalon conveyorMotor;
	public double conveySpeed = .5;
	public double bumperConveySpeed = .7;

	public void Init() {
		conveyorMotor = new CANTalon(5);
	}

	public void move() {
		if ((triggerMove() > 0) && (bumperMove() > 0)) {
			stop();
		}else {
			if (triggerMove() != 0){
				conveyorMotor.set(triggerMove());
			}else if (bumperMove() != 0){
				conveyorMotor.set(bumperMove());
			}else{
				stop();
			}	
		}
	}

	public double triggerMove() {
		if ((Robot.joystick2.getRawAxis(2) > .2) && (Robot.joystick2.getRawAxis(3) > .2)) {
			return 0;
		} else if (Robot.joystick2.getRawAxis(2) > .2) {
			return Robot.joystick2.getRawAxis(2) * conveySpeed;
		} else if (Robot.joystick2.getRawAxis(3) > .2) {
			return -Robot.joystick2.getRawAxis(3) * conveySpeed;
		} else {
			return 0;
		}
	}

	public double bumperMove() {
		if ((Robot.joystick2.getRawButton(5) == true) && (Robot.joystick2.getRawButton(6) == true)) {
			return 0;
		} else if (Robot.joystick2.getRawButton(5) == true) {
			return bumperConveySpeed;
		} else if (Robot.joystick2.getRawButton(6) == true) {
			return -bumperConveySpeed;
		} else {
			return 0;
		}
	}

	public void stop() {
		conveyorMotor.set(0);
	}

}
