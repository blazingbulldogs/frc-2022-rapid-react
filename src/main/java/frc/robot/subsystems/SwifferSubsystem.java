// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SwifferSubsystem extends SubsystemBase {

  private static class Constants {
    // TODO: Update port number
    private static final int PORT = 0;
  }

  private final TalonFX motor = new TalonFX(Constants.PORT);

  /** Creates a new SwifferSubsystem. */
  public SwifferSubsystem() {}

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void snarfs() {
    // TODO: Creat "Snarfing" action
  }

  public void shoot() {
    // TODO: Create "Shooting" action
  }

  public void stop() {
    // TODO: Create "Stoping" action
  }
}
