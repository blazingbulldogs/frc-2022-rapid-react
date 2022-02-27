// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.limelight_cargo;
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

import edu.wpi.first.math.util.Units;
import frc.robot.vision.LimelightSubsystemBase;

public class CargoLimelightSubsystem extends LimelightSubsystemBase {
  public enum Pipelines {
    RED_CARGO(0),
    BLUE_CARGO(1),
    LOADING_BAY(2),
    DRIVER_MODE(9);

    public final int index;

    Pipelines(final int index) {
      this.index = index;
    }
  }

  public final LoadingBayVisionTarget loadingBay = new LoadingBayVisionTarget(this);
  private final CargoVisionTarget redCargo =
      new CargoVisionTarget(this, CargoVisionTarget.Color.RED);
  private final CargoVisionTarget blueCargo =
      new CargoVisionTarget(this, CargoVisionTarget.Color.BLUE);
  private CargoVisionTarget ourCargoVisionTarget;
  private CargoVisionTarget opponentCargoVisionTarget;

  /** Creates a new CargoLimelightSubsystem. */
  public CargoLimelightSubsystem() {
    super("limelight-cargo", 0.0, Units.inchesToMeters(15.5), Pipelines.DRIVER_MODE.index);
  }

  public CargoVisionTarget getOurCargoVisionTarget() {
    return ourCargoVisionTarget;
  }

  public CargoVisionTarget getOpponentCargoVisionTarget() {
    return opponentCargoVisionTarget;
  }

  /** Sets the {@link CargoVisionTarget}s in this class based on what our team's alliance is. */
  public void setOurAlliance(CargoVisionTarget.Color ourAlliance) {
    final var opponentAlliance =
        ourAlliance == CargoVisionTarget.Color.RED
            ? CargoVisionTarget.Color.BLUE
            : CargoVisionTarget.Color.RED;

    ourCargoVisionTarget = getTargetForAlliance(ourAlliance);
    opponentCargoVisionTarget = getTargetForAlliance(opponentAlliance);
  }

  /** Gets the {@link CargoVisionTarget} instance for the provided alliance (red or blue). */
  private CargoVisionTarget getTargetForAlliance(CargoVisionTarget.Color alliance) {
    if (alliance == CargoVisionTarget.Color.RED) {
      return redCargo;
    }

    return blueCargo;
  }
}