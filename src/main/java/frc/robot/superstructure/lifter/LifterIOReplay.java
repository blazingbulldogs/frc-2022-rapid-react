// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.superstructure.lifter;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.system.plant.DCMotor;
import frc.robot.Constants;
import frc.robot.misc.exceptions.UnsupportedSubsystemException;

public class LifterIOReplay implements LifterIO {
  @Override
  public DCMotor getMotorSim() {
    switch (Constants.getRobot()) {
      case SIM_BOT:
        return DCMotor.getFalcon500(1);
      default:
        throw new UnsupportedSubsystemException(this);
    }
  }

  @Override
  public void updateInputs(Inputs inputs) {
    // Intentionally left empty
  }

  @Override
  public void setVoltage(double volts) {
    // Intentionally left empty
  }

  @Override
  public void setEncoderPosition(Rotation2d rotation) {
    // Intentionally left empty
  }
}
