package org.usfirst.frc.team5509.robot;

import edu.wpi.cscore.CameraServerJNI;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
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
	SendableChooser<String> chooser = new SendableChooser<String>();

	/******** Auton ************/

	// Speed Auton
	private final String AUTON_SPEED_KEY = "Auton - Speed";
	private double autonSpeed;
	public final double DEFAULT_AUTON_SPEED = .15;

	// *************************************

	// Feet Auton
	private final String FEET_TURNED_KEY = "Auton - Feet Turned";
	private double feetTurned;
	public final double DEFAULT_FEET_TURNED = 7.0;

	private final String FEET_FORWARD_KEY = "Auton - Feet Forward";
	private double feetForward;
	public final double DEFAULT_FEET_FORWARD = 9.5;

	private final String FEET_FORWARD_CENTER_KEY = "Auton - Feet Forward Center";
	private double feetForwardCenter;
	public final double DEFAULT_FEET_FORWARD_CENTER = 9.2;

	// ********************************************

	// Angles Auton
	private final String AUTON_ANGLE_KEY = "Auton - Angle to turn";
	private double angle;
	public final double DEFAULT_AUTON_ANGLE = 50;

	private final String GYRO_FIX_ANGLE_KEY = "Auton - Angle of Gyro in Phase 1 (Forward)";

	private double gyroFixAngle;
	public final double DEFAULT_GYRO_FIX_ANGLE = 1;

	// *******************************************

	/***********************/

	/******** Teleop ************/

	// Speed Teleop
	private final String SPEED_PERCENTAGE_KEY = "Teleop - Speed";
	private final String HOLD_SPEED_KEY = "Teleop - Hold Rope Speed";
	private final String BACK_CLIMB_SPEED_KEY = "Teleop - Back Rope Speed";
	private final String CLIMB_SPEED_KEY = "Teleop - Rope Speed";
	private final String CONVEYOR_SPEED_KEY = "Teleop - Conveyor Speed";
	private final String BUMPER_CONVEYOR_SPEED_KEY = "Teleop - Bumper Conveyor Speed";

	public final double DEFAULT_DRIVE_SPEED_PERCENTAGE = drive.limitPercent;
	public final double DEFAULT_HOLD_SPEED = ropeClimber.holdSpeed;
	public final double DEFAULT_BACK_ROPE_SPEED = ropeClimber.downSpeed;
	public final double DEFAULT_CLIMB_SPEED = ropeClimber.climbSpeed;
	public final double DEFAULT_CONVEYOR_SPEED = conveyorBelt.conveySpeed;
	public final double DEFAULT_BUMPER_CONVEYOR_SPEED = conveyorBelt.bumperConveySpeed;

	/*************************************/

	// Joysticks
	public static Joystick joystick1;
	public static Joystick joystick2;

	// Instantiating classes
	private static Drive drive = new Drive();
	private static ConveyorBelt conveyorBelt = new ConveyorBelt();
	private static RopeClimber ropeClimber = new RopeClimber();

	// Auton Checks
	private boolean autonCheckPhase1 = false;
	private boolean autonCheckPhase2 = false;
	private boolean autonCheckPhase3 = false;
	private boolean autonCheckPhaseCenter1 = false;

	// Sensors
	public final DigitalInput MAGNET_SENSOR = new DigitalInput(2);
	public final Ultrasonic SONAR_SENSOR = new Ultrasonic(1, 0);
	public final ADXRS450_Gyro GYRO = new ADXRS450_Gyro();
	public final static Relay light1 = new Relay(0);
	public final static Relay light2 = new Relay(1);

	// Actual numbers
	public final int ONE_REVOLUTION_IN_YOUNKINS = 1000;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		CameraServer.getInstance().startAutomaticCapture();

		joystick1 = new Joystick(0);
		joystick2 = new Joystick(1);

		chooser.addDefault("Off", "");
		chooser.addObject("Mode 1 - Left Side", "1");
		chooser.addObject("Mode 2 - Forward", "2");
		chooser.addObject("Mode 3 - Right Side", "3");

		SmartDashboard.putData("Auto choices", chooser);

		SmartDashboard.putNumber(AUTON_SPEED_KEY, DEFAULT_AUTON_SPEED);
		SmartDashboard.putNumber(SPEED_PERCENTAGE_KEY, DEFAULT_DRIVE_SPEED_PERCENTAGE);
		SmartDashboard.putNumber(HOLD_SPEED_KEY, DEFAULT_HOLD_SPEED);
		SmartDashboard.putNumber(BACK_CLIMB_SPEED_KEY, DEFAULT_BACK_ROPE_SPEED);
		SmartDashboard.putNumber(CLIMB_SPEED_KEY, DEFAULT_CLIMB_SPEED);
		SmartDashboard.putNumber(CONVEYOR_SPEED_KEY, DEFAULT_CONVEYOR_SPEED);
		SmartDashboard.putNumber(BUMPER_CONVEYOR_SPEED_KEY, DEFAULT_BUMPER_CONVEYOR_SPEED);
		SmartDashboard.putNumber(AUTON_ANGLE_KEY, DEFAULT_AUTON_ANGLE);
		SmartDashboard.putNumber(FEET_FORWARD_CENTER_KEY, DEFAULT_FEET_FORWARD_CENTER);
		SmartDashboard.putNumber(FEET_FORWARD_KEY, DEFAULT_FEET_FORWARD);
		SmartDashboard.putNumber(FEET_TURNED_KEY, DEFAULT_FEET_TURNED);
		SmartDashboard.putNumber(GYRO_FIX_ANGLE_KEY, DEFAULT_GYRO_FIX_ANGLE);

		GYRO.calibrate();

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
		autonSpeed = SmartDashboard.getNumber(AUTON_SPEED_KEY, DEFAULT_AUTON_SPEED);
		angle = SmartDashboard.getNumber(AUTON_ANGLE_KEY, DEFAULT_AUTON_ANGLE);
		feetForwardCenter = SmartDashboard.getNumber(FEET_FORWARD_CENTER_KEY, DEFAULT_FEET_FORWARD_CENTER);
		feetForward = SmartDashboard.getNumber(FEET_FORWARD_KEY, DEFAULT_FEET_FORWARD);
		feetTurned = SmartDashboard.getNumber(FEET_TURNED_KEY, DEFAULT_FEET_TURNED);
		gyroFixAngle = SmartDashboard.getNumber(GYRO_FIX_ANGLE_KEY, DEFAULT_GYRO_FIX_ANGLE);

		drive.backLeftMotor.setEncPosition(0);
		drive.backRightMotor.setEncPosition(0);
		drive.frontLeftMotor.setEncPosition(0);
		drive.frontRightMotor.setEncPosition(0);

		autonCheckPhase1 = false;
		autonCheckPhase2 = false;
		autonCheckPhase3 = false;

		autonCheckPhaseCenter1 = false;
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		lightsSet(.2);

		switch (autoSelected) {
		case "1":
			if (autonCheckPhase1 == false) {
				autonSideMovePhase1(feetForward);
			} else if (autonCheckPhase2 == false) {
				autonSideMovePhase2(angle, 1);
			} else if (autonCheckPhase3 == false) {
				autonSideMovePhase3(feetTurned);
			}
			break;
		case "2":
			if (autonCheckPhaseCenter1 == false) {
				autonMoveForwardCenter(feetForwardCenter);
			} else {
				drive.stop();
			}
			break;
		case "3":
			if (autonCheckPhase1 == false) {
				autonSideMovePhase1(feetForward);
			} else if (autonCheckPhase2 == false) {
				autonSideMovePhase2(angle, -1);
			} else if (autonCheckPhase3 == false) {
				autonSideMovePhase3(feetTurned);
			}
			break;
		default:
			break;
		}
	}

	private void autonMoveForwardCenter(double feet) {
		if (feet > 0) {
			if (GYRO.getAngle() > gyroFixAngle) {
				drive.backRightMotor.set(autonSpeed + .125);
				drive.frontRightMotor.set(autonSpeed + .125);
				drive.backLeftMotor.set(autonSpeed);
				drive.frontLeftMotor.set(autonSpeed);
			} else if (GYRO.getAngle() < -gyroFixAngle) {
				drive.backRightMotor.set(autonSpeed);
				drive.frontRightMotor.set(autonSpeed);
				drive.backLeftMotor.set(autonSpeed + .125);
				drive.frontLeftMotor.set(autonSpeed + .125);
			} else if (younkinsToRevolutions() < feet) {
				drive.backRightMotor.set(autonSpeed);
				drive.frontRightMotor.set(autonSpeed);
				drive.backLeftMotor.set(autonSpeed);
				drive.frontLeftMotor.set(autonSpeed);
			} else {
				drive.stop();
				autonCheckPhaseCenter1 = true;
				drive.backLeftMotor.setPosition(0);
				drive.backRightMotor.setPosition(0);
				drive.frontRightMotor.setPosition(0);
				drive.frontLeftMotor.setPosition(0);
			}
		}
	}

	private void autonSideMovePhase1(double feet) {
		if (GYRO.getAngle() > gyroFixAngle) {
			drive.backRightMotor.set(autonSpeed + .1);
			drive.frontRightMotor.set(autonSpeed + .1);
			drive.backLeftMotor.set(autonSpeed);
			drive.frontLeftMotor.set(autonSpeed);
		} else if (GYRO.getAngle() < -gyroFixAngle) {
			drive.backRightMotor.set(autonSpeed);
			drive.frontRightMotor.set(autonSpeed);
			drive.backLeftMotor.set(autonSpeed + .1);
			drive.frontLeftMotor.set(autonSpeed + .1);
		} else if (younkinsToRevolutions() < feet) {
			drive.backRightMotor.set(autonSpeed + .05);
			drive.frontRightMotor.set(autonSpeed + .05);
			drive.backLeftMotor.set(autonSpeed + .05);
			drive.frontLeftMotor.set(autonSpeed + .05);
		} else {
			drive.stop();
			autonCheckPhase1 = true;
			drive.backLeftMotor.setPosition(0);
			drive.backRightMotor.setPosition(0);
			drive.frontRightMotor.setPosition(0);
			drive.frontLeftMotor.setPosition(0);
		}
	}

	private void autonSideMovePhase2(double gryoAngle, int posOrNeg) {
		if (Math.abs(GYRO.getAngle()) < gryoAngle) {
			drive.backLeftMotor.set((autonSpeed + .05) * posOrNeg);
			drive.frontLeftMotor.set((autonSpeed + .05) * posOrNeg);
			drive.backRightMotor.set((-autonSpeed - .05) * posOrNeg);
			drive.frontRightMotor.set((-autonSpeed - .05) * posOrNeg);
		} else {
			drive.stop();
			autonCheckPhase2 = true;
			drive.backLeftMotor.setPosition(0);
			drive.backRightMotor.setPosition(0);
			drive.frontRightMotor.setPosition(0);
			drive.frontLeftMotor.setPosition(0);
		}
	}

	private void autonSideMovePhase3(double feet) {
		if (younkinsToRevolutions() < feet) {
			drive.backRightMotor.set(autonSpeed);
			drive.frontRightMotor.set(autonSpeed);
			drive.backLeftMotor.set(autonSpeed);
			drive.frontLeftMotor.set(autonSpeed);
		} else {
			drive.stop();
			autonCheckPhase3 = true;
			drive.backLeftMotor.setPosition(0);
			drive.backRightMotor.setPosition(0);
			drive.frontRightMotor.setPosition(0);
			drive.frontLeftMotor.setPosition(0);
		}
	}

	@Override
	public void teleopInit() {
		drive.stop();
		drive.limitPercent = SmartDashboard.getNumber(SPEED_PERCENTAGE_KEY, DEFAULT_DRIVE_SPEED_PERCENTAGE);
		ropeClimber.holdSpeed = SmartDashboard.getNumber(HOLD_SPEED_KEY, DEFAULT_HOLD_SPEED);
		ropeClimber.downSpeed = SmartDashboard.getNumber(BACK_CLIMB_SPEED_KEY, DEFAULT_BACK_ROPE_SPEED);
		ropeClimber.climbSpeed = SmartDashboard.getNumber(CLIMB_SPEED_KEY, DEFAULT_CLIMB_SPEED);
		conveyorBelt.conveySpeed = SmartDashboard.getNumber(CONVEYOR_SPEED_KEY, DEFAULT_CONVEYOR_SPEED);
		conveyorBelt.bumperConveySpeed = SmartDashboard.getNumber(BUMPER_CONVEYOR_SPEED_KEY,
				DEFAULT_BUMPER_CONVEYOR_SPEED);

		light1.set(Relay.Value.kOn);
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		drive.move();
		ropeClimber.move();
		conveyorBelt.move();
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
		lightsSet(.15);
	}

	public static void lightsSet(double time) {
		light2.set(Relay.Value.kOn);
		light1.set(Relay.Value.kOff);
		Timer.delay(time);
		light1.set(Relay.Value.kOn);
		light2.set(Relay.Value.kOff);
		Timer.delay(time);
	}

	@Override
	public void disabledInit() {
	}

	// Returns how far the robot has moved in feet
	private double younkinsToRevolutions() {
		double avg = (Math.abs(drive.backLeftMotor.getPosition()) + Math.abs(drive.backRightMotor.getPosition())
				+ Math.abs(drive.frontLeftMotor.getPosition()) + Math.abs(drive.frontRightMotor.getPosition())) / 4;
		System.out.println("Avg" + avg);
		double num = Math.abs(avg) / ONE_REVOLUTION_IN_YOUNKINS;
		num *= 25.13;
		num /= 12;
		return num;
	}

}
