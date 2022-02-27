// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.drive;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.sensors.SensorVelocityMeasPeriod;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import io.github.oblarg.oblog.Loggable;
import io.github.oblarg.oblog.annotations.Log;

/** This class should only be used within {@link DriveSubsystem} and {@link Drivebase}. */
public class Wheel implements Loggable {
  /** The maximum velocity of a wheel in meters/second. */
  public static final double MAX_WHEEL_VELOCITY =
      Wheel.encoderRotationsToMeters(Constants.MAX_ENCODER_ROTATIONS_PER_SECOND);

  public static final SimpleMotorFeedforward FEEDFORWARD =
      new SimpleMotorFeedforward(0.61761, 2.3902, 0.17718);

  private static class Constants {
    /** The circumference of the wheel in meters. */
    public static final double CIRCUMFERENCE =
        // Wheel diameter * pi is the circumference
        // 0.476 meters
        Units.inchesToMeters(5.97) * Math.PI;

    /** The number of rotations of the encoder can do in 1 second at maximum speed. */
    public static final double MAX_ENCODER_ROTATIONS_PER_SECOND = 22197;
    /** The number of encoder rotations for the wheel to rotate once. */
    public static final double ENCODER_ROTATIONS_PER_WHEEL_ROTATION = 2048 * 10.7;

    public static final double MAX_MOTOR_VOLTAGE = 12;
  }

  /** Convert encoder rotations to meters this wheel has travelled since last being reset. */
  private static double encoderRotationsToMeters(double encoderRotations) {
    final var wheelRotations = encoderRotations / Constants.ENCODER_ROTATIONS_PER_WHEEL_ROTATION;

    return wheelRotations * Constants.CIRCUMFERENCE;
  }

  /**
   * The position of the wheel corresponding to this motor, relative to the robot center, in meters.
   * Used for kinematics.
   */
  public final Translation2d positionToCenterOfRobot;

  public final WPI_TalonFX motor;

  /**
   * Wheel velocity PID controller.
   *
   * <p>Input is current velocity in meters/second.
   *
   * <p>Output is motor output in volts.
   *
   * <p>Setpoint: target velocity in meters/second
   */
  // TODO: Experiment with different values for max velocity error and max control effort
  // Remember to check and uncheck "Convert Gains to Encoder Counts" to trigger a refresh and fix
  // the Kp value. Consider rerunning both the quasistatic ramp rate test and the dynamic step
  // voltage test with the robot travelling with 12.0V of energy. This may be unnecessary since the
  // Falcon 500 motors have a pretty linear output of speed:voltage. When recording test results
  // make a note of what voltages were used.
  //
  // Error=1.2m/s MaxControlEffort=7.0V -> Kp = 2.7182 (DEFAULT)
  // Error=0.3m/s MaxControlEffort=12.0V -> Kp = 7.2692
  // Error=0.3m/s MaxControlEffort=7.0V -> Kp = 6.5871
  // Error=0.3m/s MaxControlEffort=7.0V -> Kp = 6.5871
  // Error=0.8m/s MaxControlEffort=7.0V -> Kp = 3.9486
  private final PIDController velocityPid = new PIDController(2.7182, 0, 0);

  /**
   * This wheel's name.
   *
   * @example frontLeft
   */
  private final String name;

  public Wheel(String name, int motorPort, Translation2d positionToCenterOfRobot) {
    this.name = name;
    this.motor = new WPI_TalonFX(motorPort);
    this.positionToCenterOfRobot = positionToCenterOfRobot;

    velocityPid.setSetpoint(0);

    // TODO: These values probably need to be tuned - see tuning instructions
    // https://docs.ctre-phoenix.com/en/stable/ch14_MCSensor.html#recommended-procedure
    motor.configVelocityMeasurementPeriod(SensorVelocityMeasPeriod.Period_1Ms);
    motor.configVelocityMeasurementWindow(1);
  }

  @Override
  public String configureLogName() {
    return "Wheel " + name;
  }

  /**
   * Sets the motor output to meet the desired velocity configured with {@link
   * Wheel#setDesiredVelocity(double)}.
   */
  public void doVelocityControlLoop() {
    motor.setVoltage(velocityToVoltage(velocityPid.getSetpoint()));
  }

  /**
   * Set desired velocity for the motor.
   *
   * @param metersPerSecond The desired velocity in meters/second
   */
  public void setDesiredVelocity(double metersPerSecond) {
    velocityPid.setSetpoint(metersPerSecond);
  }

  /** Get this wheel's velocity in meters/second. */
  @Log
  public double getVelocity() {
    // This is definitely per 100ms
    final var nativePer100Ms = motor.getSelectedSensorVelocity();
    final var nativePerSecond = nativePer100Ms * 10;

    return encoderRotationsToMeters(nativePerSecond);
  }

  /** Get the distance in meters this wheel's encoder has travelled since last being reset. */
  @Log
  public double getDistance() {
    final var nativeDistance = motor.getSelectedSensorPosition();

    return encoderRotationsToMeters(nativeDistance);
  }

  /** Zeroes the encoder position. */
  public void zeroEncoder() {
    motor.setSelectedSensorPosition(0);
  }

  /** Converts a velocity in meters/second to a voltage. */
  private double velocityToVoltage(double velocity) {
    final var rawVoltage = FEEDFORWARD.calculate(velocity) + velocityPid.calculate(velocity);

    return MathUtil.clamp(rawVoltage, -Constants.MAX_MOTOR_VOLTAGE, Constants.MAX_MOTOR_VOLTAGE);
  }
}
