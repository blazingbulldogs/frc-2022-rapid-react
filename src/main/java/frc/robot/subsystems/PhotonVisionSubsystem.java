// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.photonvision.PhotonCamera;

public class PhotonVisionSubsystem extends SubsystemBase {
  public final PhotonCamera camera;

  /** Creates a new PhotonVisionSubsystem with the default camera name ("photonvision"). */
  public PhotonVisionSubsystem() {
    this("photonvision");
  }

  /** Creates a new PhotonVisionSubsystem. */
  public PhotonVisionSubsystem(String cameraName) {
    camera = new PhotonCamera(cameraName);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
