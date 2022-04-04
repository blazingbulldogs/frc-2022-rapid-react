// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.controller;

import edu.wpi.first.math.filter.SlewRateLimiter;

public class DriveController extends ButtonController {
  private final LogitechF310DirectInputController controller;
  private final SlewRateLimiter xLimiter = new SlewRateLimiter(7);
  private final SlewRateLimiter yLimiter = new SlewRateLimiter(7);

  public DriveController(LogitechF310DirectInputController controller) {
    super(controller);

    this.controller = controller;
  }

  /** Scale a joystick value. */
  private static double joystickScale(double x) {
    return Math.signum(x) * Math.pow(x, 2);
  }

  /** The rotation across the robot's x-axis as a percentage (<code>-1 <= x <= 1</code>) */
  public double getXPercentage() {
    return joystickScale(xLimiter.calculate(controller.getLeftX()));
  }

  /** The translation across the robot's y-axis as a percentage (<code>-1 <= x <= 1</code>) */
  public double getYPercentage() {
    return joystickScale(yLimiter.calculate(controller.getLeftY()));
  }

  /** The rotation about the robot's z-axis as a percentage (<code>-1 <= x <= 1</code>) */
  public double getThetaPercentage() {
    // This is not ratelimited since heading control is already motion profiled
    return joystickScale(controller.getRightX());
  }
}
