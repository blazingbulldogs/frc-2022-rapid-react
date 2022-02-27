// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.drive;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.SPI;

public class Gyro {
  private final AHRS sensor = new AHRS(SPI.Port.kMXP);

  /** Creates a new Gyro. */
  public Gyro() {
    sensor.calibrate();
  }

  /** Zeroes the heading of the robot. */
  public void zeroHeading() {
    sensor.reset();
  }

  /** Returns the turn rate of the robot in radians/second. */
  public double getTurnRate() {
    return Units.degreesToRadians(-sensor.getRate());
  }

  /** @see AHRS#getRotation2d() */
  public Rotation2d getRotation() {
    return sensor.getRotation2d();
  }
}
