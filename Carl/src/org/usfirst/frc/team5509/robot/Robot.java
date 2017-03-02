package org.usfirst.frc.team5509.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	// Smart Dashboard
	String autoSelected;
	SendableChooser<String> chooser;

	final String autonSpeedKey = "autonSpeed";
	final String speedPerKey = "speed%";
	final String climbSpeedKey = "Rope Speed";
	final String conveySpeedKey = "Conveyor Speed";
	final String bumperConveySpeedKey = "Bumper Conveyor Speed";
	final String feetTurnedKey = "Feet Turned";
	final String feetForwardKey = "Feet Forward";
	final String feetForwardCenterKey = "Feet Forward Center";
	final String autonAngleKey = "Auton Angle";
	final String gyroFixAngleKey = "Angle of Gyro in Phase 1";

	public static Joystick joystick1;
	public static Joystick joystick2;

	public Relay light = new Relay(0);

	private static Drive drive = new Drive();
	private static ConveyorBelt conveyorBelt = new ConveyorBelt();
	private static RopeClimber ropeClimber = new RopeClimber();

	public Timer time = new Timer();
	public ADXRS450_Gyro gyro = new ADXRS450_Gyro();

	private boolean autonCheckPhase1 = false;
	private boolean autonCheckPhase2 = false;
	private boolean autonCheckPhase3 = false;
	private boolean autonCheckPhase4 = false;

	public double autonSpeed;
	public double feetForwardCenter;
	public double feetForward;
	public double feetTurned;
	public double angle;
	public double gyroFixAngle;

	final double defaultAutonSpeed = .15;
	public double defaultFeetForwardCenter = 5.8;
	public double defaultFeetForward = 4.5;
	public double defaultFeetTurned = 5.9;
	public double defaultAngle = 60;
	public double defaultGyroFixAngle = 2.5;
	final double defaultDrivePer = drive.limitPercent;
	final double defaultClimbSpeed = ropeClimber.climbSpeed;
	final double defaultConveySpeed = conveyorBelt.conveySpeed;
	final double defaultBumperConveySpeed = conveyorBelt.bumperConveySpeed;

	public int oneRevolutionInYounkins = 1400;

	// Sensors
	public DigitalInput magSensor = new DigitalInput(2);
	Ultrasonic sonar = new Ultrasonic(1, 0);

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		joystick1 = new Joystick(0);
		joystick2 = new Joystick(1);

		chooser = new SendableChooser<String>();
		chooser.addDefault("Off", "");
		chooser.addObject("Mode 1 - Left Side", "1");
		chooser.addObject("Mode 2 - Forward", "2");
		chooser.addObject("Mode 3 - Right Side", "3");

		SmartDashboard.putData("Auto choices", chooser);

		SmartDashboard.putNumber(autonSpeedKey, defaultAutonSpeed);
		SmartDashboard.putNumber(speedPerKey, defaultDrivePer);
		SmartDashboard.putNumber(climbSpeedKey, defaultClimbSpeed);
		SmartDashboard.putNumber(conveySpeedKey, defaultConveySpeed);
		SmartDashboard.putNumber(bumperConveySpeedKey, defaultBumperConveySpeed);
		SmartDashboard.putNumber(autonAngleKey, defaultAngle);
		SmartDashboard.putNumber(feetForwardCenterKey, defaultFeetForwardCenter);
		SmartDashboard.putNumber(feetForwardKey, defaultFeetForward);
		SmartDashboard.putNumber(feetTurnedKey, defaultFeetTurned);
		SmartDashboard.putNumber(gyroFixAngleKey, defaultGyroFixAngle);

		gyro.calibrate();

		conveyorBelt.Init();
		drive.Init();
		ropeClimber.Init();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autoSelected = chooser.getSelected();

		System.out.println("Auto selected: " + autoSelected);

		drive.stop();

		SmartDashboard.putString("Auton Mode", autoSelected);
		autonSpeed = SmartDashboard.getNumber(autonSpeedKey, defaultAutonSpeed);
		angle = SmartDashboard.getNumber(autonAngleKey, defaultAngle);
		feetForwardCenter = SmartDashboard.getNumber(feetForwardCenterKey, defaultFeetForwardCenter);
		feetForward = SmartDashboard.getNumber(feetForwardKey, defaultFeetForward);
		feetTurned = SmartDashboard.getNumber(feetTurnedKey, defaultFeetTurned);
		gyroFixAngle = SmartDashboard.getNumber(gyroFixAngleKey, defaultGyroFixAngle);

		drive.backLeftMotor.setEncPosition(0);
		drive.backRightMotor.setEncPosition(0);
		drive.frontLeftMotor.setEncPosition(0);
		drive.frontRightMotor.setEncPosition(0);

		autonCheckPhase1 = false;
		autonCheckPhase2 = false;
		autonCheckPhase3 = false;
		autonCheckPhase4 = false;
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
		case "1":
			if (autonCheckPhase1 == false) {
				autonMovePhase1(feetForward);
			} else if (autonCheckPhase2 == false) {
				autonMovePhase2(angle, 1);
			} else if (autonCheckPhase3 == false) {
				autonMovePhase3(feetTurned);
			}
			break;
		case "2":
			System.out.println(autonCheckPhase1 + " " + younkinsToRevolutions());
			if (autonCheckPhase1 == false) {
				autonMoveForward(feetForwardCenter);
			} else {
				drive.stop();
			}
			break;
		case "3":
			if (autonCheckPhase1 == false) {
				autonMovePhase1(feetForward);
			} else if (autonCheckPhase2 == false) {
				autonMovePhase2(55, -1);
			} else if (autonCheckPhase3 == false) {
				autonMovePhase3(feetTurned);
			}
			break;
		default:
			System.out.println(gyro.getAngle());
			break;
		}
	}

	// Returns how far the robot has moved in feet
	// Possible todos: add a parameter to autonMovePhase1 (and 3) for number of
	// feet to go, add new input to smart dashboard to set that parameter - so
	// you can adjust distance for phases on the fly.
	// Also for phase 2?
	private double younkinsToRevolutions() {
		double num = (Math.abs(drive.backLeftMotor.getPosition())) / oneRevolutionInYounkins;
		num *= 25.13;
		num /= 12;

		System.out.println(num);

		return num;
	}

	private void autonMoveForward(double feet) {
		if (gyro.getAngle() > gyroFixAngle) {
			drive.backRightMotor.set(autonSpeed + .05);
			drive.frontRightMotor.set(autonSpeed + .05);
			drive.backLeftMotor.set(autonSpeed);
			drive.frontLeftMotor.set(autonSpeed);
		} else if (gyro.getAngle() < -gyroFixAngle) {
			drive.backRightMotor.set(autonSpeed);
			drive.frontRightMotor.set(autonSpeed);
			drive.backLeftMotor.set(autonSpeed + .05);
			drive.frontLeftMotor.set(autonSpeed + .05);
		} else if (younkinsToRevolutions() < feet - .1) {
			drive.backRightMotor.set(autonSpeed);
			drive.frontRightMotor.set(autonSpeed);
			drive.backLeftMotor.set(autonSpeed);
			drive.frontLeftMotor.set(autonSpeed);
		} else if (younkinsToRevolutions() > feet + .1) {
			drive.backRightMotor.set(-autonSpeed);
			drive.frontRightMotor.set(-autonSpeed);
			drive.backLeftMotor.set(-autonSpeed);
			drive.frontLeftMotor.set(-autonSpeed);
		} else {
			drive.stop();
			autonCheckPhase1 = true;
			drive.backLeftMotor.setPosition(0);
		}
	}

	private void autonMovePhase1(double feet) {
		if (gyro.getAngle() > gyroFixAngle) {
			drive.backRightMotor.set(autonSpeed + .05);
			drive.frontRightMotor.set(autonSpeed + .05);
			drive.backLeftMotor.set(autonSpeed);
			drive.frontLeftMotor.set(autonSpeed);
		} else if (gyro.getAngle() < -gyroFixAngle) {
			drive.backRightMotor.set(autonSpeed);
			drive.frontRightMotor.set(autonSpeed);
			drive.backLeftMotor.set(autonSpeed + .05);
			drive.frontLeftMotor.set(autonSpeed + .05);
		} else if (younkinsToRevolutions() < feet - .1) {
			drive.backRightMotor.set(autonSpeed + .05);
			drive.frontRightMotor.set(autonSpeed + .05);
			drive.backLeftMotor.set(autonSpeed + .05);
			drive.frontLeftMotor.set(autonSpeed + .05);
		} else if (younkinsToRevolutions() > feet + .1) {
			drive.backRightMotor.set(-autonSpeed - .05);
			drive.frontRightMotor.set(-autonSpeed - .05);
			drive.backLeftMotor.set(-autonSpeed - .05);
			drive.frontLeftMotor.set(-autonSpeed - .05);
		} else {
			drive.stop();
			autonCheckPhase1 = true;
			drive.backLeftMotor.setPosition(0);
		}
	}

	private void autonMovePhase2(double gryoAngle, int posOrNeg) {
		if (Math.abs(gyro.getAngle()) < gryoAngle - 1) {
			drive.backLeftMotor.set((autonSpeed + .05) * posOrNeg);
			drive.frontLeftMotor.set((autonSpeed + .05) * posOrNeg);
			drive.backRightMotor.set((-autonSpeed - .05) * posOrNeg);
			drive.frontRightMotor.set((-autonSpeed - .05) * posOrNeg);
		} else if (Math.abs(gyro.getAngle()) > gryoAngle + 1) {
			drive.backLeftMotor.set((-autonSpeed - .05) * posOrNeg);
			drive.frontLeftMotor.set((-autonSpeed - .05) * posOrNeg);
			drive.backRightMotor.set((autonSpeed + .05) * posOrNeg);
			drive.frontRightMotor.set((autonSpeed + .05) * posOrNeg);
		} else {
			drive.stop();
			autonCheckPhase2 = true;
			drive.backLeftMotor.setPosition(0);
		}
	}

	private void autonMovePhase3(double feet) {
		System.out.println(drive.backLeftMotor.getEncPosition());

		if (younkinsToRevolutions() < feet) {
			drive.backRightMotor.set(autonSpeed);
			drive.frontRightMotor.set(autonSpeed);
			drive.backLeftMotor.set(autonSpeed);
			drive.frontLeftMotor.set(autonSpeed);
		} else {
			drive.stop();
			autonCheckPhase3 = true;
			drive.backLeftMotor.setPosition(0);
		}
	}

	@Override
	public void teleopInit() {
		drive.stop();
		drive.limitPercent = SmartDashboard.getNumber(speedPerKey, defaultDrivePer);
		ropeClimber.climbSpeed = SmartDashboard.getNumber(climbSpeedKey, defaultClimbSpeed);
		conveyorBelt.conveySpeed = SmartDashboard.getNumber(conveySpeedKey, defaultConveySpeed);
		conveyorBelt.bumperConveySpeed = SmartDashboard.getNumber(bumperConveySpeedKey, defaultBumperConveySpeed);
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		drive.move();
		conveyorBelt.move();
		ropeClimber.move();

		if (joystick1.getRawButton(1) == true) {
			SmartDashboard.putBoolean("ButtonPressed", true);
			light.set(Relay.Value.kOn);
		}
		if (joystick1.getRawButton(1) == false) {
			SmartDashboard.putBoolean("ButtonPressed", false);
			light.set(Relay.Value.kOff);
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}

	@Override
	public void robotPeriodic() {
	}

	@Override
	public void disabledPeriodic() {
	}

	@Override
	public void disabledInit() {
	}
}
