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

	final String autonDelayKey = "autonDelay";
	final String autonTimeKey = "autonTime";
	final String autonSpeedKey = "autonSpeed";
	final String speedPerKey = "speed%";
	final String climbSpeedKey = "Rope Speed";
	final String conveySpeedKey = "Conveyor Speed";

	final double defaultDrivePer = drive.limitPercent;
	final double defaultClimbSpeed = ropeClimber.climbSpeed;
	final double defaultConveySpeed = conveyorBelt.conveySpeed;
	final double defaultAutonSpeed = .15;

	double autonTime;
	double autonSpeed;

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
		chooser.addObject("Mode 1 - Move Forward", "1");
		chooser.addObject("Mode 2 - something", "2");
		chooser.addObject("Mode 3 - something", "3");

		SmartDashboard.putData("Auto choices", chooser);
		SmartDashboard.putNumber(autonDelayKey, 0);
		SmartDashboard.putNumber(autonTimeKey, 0);
		SmartDashboard.putNumber(autonSpeedKey, 0);
		SmartDashboard.putNumber(speedPerKey, defaultDrivePer);
		SmartDashboard.putNumber(climbSpeedKey, defaultClimbSpeed);
		SmartDashboard.putNumber(conveySpeedKey, defaultConveySpeed);

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

		double delay = SmartDashboard.getNumber(autonDelayKey, 0);
		autonSpeed = SmartDashboard.getNumber(autonSpeedKey, defaultAutonSpeed);

		System.out.println("Time Delay " + delay);
		time.start();

		drive.backLeftMotor.setEncPosition(0);
		
		gyro.calibrate();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
		case "1":
			if (autonCheckPhase1 == false) {
				autonMovePhase1();
			} else if (autonCheckPhase2 == false) {
				autonMovePhase2(60, 1);
			} else if (autonCheckPhase3 == false) {
				autonMovePhase3();
			}
			break;
		case "2":
			if (autonCheckPhase1 == false) {
				autonMovePhase1();
			} else {
				drive.stop();
			}
			break;
		case "3":
			if (autonCheckPhase1 == false) {
				autonMovePhase1();
			} else if (autonCheckPhase2 == false) {
				autonMovePhase2(60, -1);
			} else if (autonCheckPhase3 == false) {
				autonMovePhase3();
			}
			break;
		default:
			System.out.println(gyro.getAngle());
			break;
		}
	}

	private double feetToYounkinRevolutions() {
		double num = (Math.abs(drive.backLeftMotor.getPosition())) / oneRevolutionInYounkins;
		num = num * 25.13;
		num = num / 12;
		return num;
	}

	private void autonMovePhase1() {
		System.out.println(drive.backLeftMotor.getEncPosition());

		if (feetToYounkinRevolutions() < 4.9) {
			drive.backRightMotor.set(autonSpeed);
			drive.frontRightMotor.set(autonSpeed);
			drive.backLeftMotor.set(autonSpeed);
			drive.frontLeftMotor.set(autonSpeed);
		} else if (feetToYounkinRevolutions() > 5.1) {
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

	private void autonMovePhase2(int gryoAngle, int posOrNeg) {
		if (Math.abs(gyro.getAngle()) < gryoAngle - 1) {
			drive.backLeftMotor.set(autonSpeed * posOrNeg);
			drive.frontLeftMotor.set(autonSpeed * posOrNeg);
			drive.backRightMotor.set(-autonSpeed * posOrNeg);
			drive.frontRightMotor.set(-autonSpeed * posOrNeg);
		} else if (Math.abs(gyro.getAngle()) > gryoAngle + 1) {
			drive.backLeftMotor.set(-autonSpeed * posOrNeg);
			drive.frontLeftMotor.set(-autonSpeed * posOrNeg);
			drive.backRightMotor.set(autonSpeed * posOrNeg);
			drive.frontRightMotor.set(autonSpeed * posOrNeg);
		} else {
			drive.stop();
			autonCheckPhase2 = true;
			drive.backLeftMotor.setPosition(0);
		}
	}

	private void autonMovePhase3() {
		System.out.println(drive.backLeftMotor.getEncPosition());

		if (feetToYounkinRevolutions() < 2.9) {
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
		double speedPer = SmartDashboard.getNumber(speedPerKey, defaultDrivePer);
		double ropeSpeed = SmartDashboard.getNumber(climbSpeedKey, defaultClimbSpeed);
		double conveySpeed = SmartDashboard.getNumber(conveySpeedKey, defaultConveySpeed);
		drive.limitPercent = speedPer;
		ropeClimber.climbSpeed = ropeSpeed;
		conveyorBelt.conveySpeed = conveySpeed;

		drive.backLeftMotor.setEncPosition(0);

		sonar.setEnabled(true);
		sonar.setAutomaticMode(true);
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
