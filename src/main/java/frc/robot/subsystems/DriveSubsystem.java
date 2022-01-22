// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.MecanumDriveKinematics;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import io.github.oblarg.oblog.annotations.Config;

// THE PLAN:

// Teleop:
// Use percentage control with the motors.
// There is no motion profiling or PID controllers or anything.
// Slew ratelimiting can be used to get similar effects as motion profiling.

// Auto:
// 1. Somehow, someone will call driveWithSpeeds() with a goal ChassisSpeeds (x, y, and omega).
// 2. Use kinematics to convert the ChassisSpeeds -> MecanumDriveWheelSpeeds
// 3. Closed loop velocity control with feedforward for each wheel

// Limelight:
// 1. The Limelight tells us the x, y, and omega error for aligning with a vision target.
//    This is a Pose2d.
// 2. Because we know the maximum possible velocity for x, y, and omega on our robot we can use that
//    to construct a ChassisSpeeds object.
//    If trying to go maximum speed is too fast, you can try using 50% of the max velocities before.
// 3. This object is given to driveWithSpeeds() (see above).

public class DriveSubsystem extends SubsystemBase {
  private static final class Constants {
    // Max of 1 rotation per second (2π radians per second) and max acceleration of 0.5 rotations
    // per second squared (π radians per second squared)
    private static final TrapezoidProfile.Constraints MAX_ROTATION =
        new TrapezoidProfile.Constraints(2 * Math.PI, Math.PI);

    private static double p = 0.25;
    private static final double i = 0;
    private static final double d = 0;
  }

  // TODO: Replace these placeholder values
  private final SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(0.1, 0.1);

  // TODO: Replace these placeholder distance/revolution ratios in EncoderConstants
  private final Wheel frontLeft =
      new Wheel(
          new Wheel.MotorConstants(10, new Translation2d(0.285, 0.285)),
          new Wheel.EncoderConstants(22300 * 10, 3428340),
          feedforward,
          new PIDController(Constants.p, Constants.i, Constants.d),
          Units.inchesToMeters(18.75));
  private final Wheel frontRight =
      new Wheel(
          new Wheel.MotorConstants(11, new Translation2d(0.285, -0.285)),
          new Wheel.EncoderConstants(22300 * 10, 3428340),
          feedforward,
          new PIDController(Constants.p, Constants.i, Constants.d),
          Units.inchesToMeters(18.75));
  private final Wheel rearLeft =
      new Wheel(
          new Wheel.MotorConstants(12, new Translation2d(-0.285, 0.285)),
          new Wheel.EncoderConstants(22300 * 10, 3428340),
          feedforward,
          new PIDController(Constants.p, Constants.i, Constants.d),
          Units.inchesToMeters(18.75));
  private final Wheel rearRight =
      new Wheel(
          new Wheel.MotorConstants(13, new Translation2d(-0.285, -0.28)),
          new Wheel.EncoderConstants(22300 * 10, 3428340),
          feedforward,
          new PIDController(Constants.p, Constants.i, Constants.d),
          Units.inchesToMeters(18.75));

  // TODO: Tune these values - currently they are just copy-pasted from 2020 (which is probably not
  // well-tuned either)
  private final PIDController xPid = new PIDController(0.03, 0, 0.006);
  private final PIDController yPid = new PIDController(0.03, 0, 0.004);
  private final ProfiledPIDController thetaPid =
      new ProfiledPIDController(0.1, 0, 0.0003, Constants.MAX_ROTATION);

  public final HolonomicDriveController driveController =
      new HolonomicDriveController(xPid, yPid, thetaPid);

  private final MecanumDrive drive =
      new MecanumDrive(frontLeft.motor, rearLeft.motor, frontRight.motor, rearRight.motor);

  private final MecanumDriveKinematics kinematics =
      new MecanumDriveKinematics(
          frontLeft.motorConstants.position,
          frontRight.motorConstants.position,
          rearLeft.motorConstants.position,
          rearRight.motorConstants.position);

  /** Creates a new DriveSubsystem. */
  public DriveSubsystem() {
    frontRight.motor.setInverted(true);
    rearRight.motor.setInverted(true);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void driveTeleop(double xPercentage, double yPercentage, double thetaPercentage) {
    // drive.setSafetyEnabled(true);

    // TODO: See if you need to explicitly use percentage control mode here - I'm pretty sure this
    // works as-is though
    // drive.driveCartesian(-yPercentage, xPercentage, thetaPercentage);
  }

  /** Stops all the motors. */
  public void stopMotors() {
    drive.stopMotor();
  }

  public void driveWithSpeeds(ChassisSpeeds chassisSpeeds) {
    drive.setSafetyEnabled(false);

    final var wheelSpeeds = kinematics.toWheelSpeeds(chassisSpeeds);

    frontLeft.setVelocity(wheelSpeeds.frontLeftMetersPerSecond);
    frontRight.setVelocity(wheelSpeeds.frontRightMetersPerSecond);
    rearLeft.setVelocity(wheelSpeeds.rearLeftMetersPerSecond);
    rearRight.setVelocity(wheelSpeeds.rearRightMetersPerSecond);

    frontLeft.periodic();
    frontRight.periodic();
    rearLeft.periodic();
    rearRight.periodic();
  }
}
