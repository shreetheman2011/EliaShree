// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.hopper.Hopper;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.swerve.CommandSwerveDrivetrain;
import frc.robot.utils.RobotManager;
import frc.robot.utils.RobotState;


public class RobotContainer {

  private Keybindings keybinds;
  private Intake intake;
  private Hopper hopper;
  private Shooter shooter;
  private RobotManager robotManager;
  private final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
  

  public RobotContainer() {
    intake = new Intake();
    hopper = new Hopper();
    shooter = new Shooter();
    robotManager = new RobotManager(intake, hopper, shooter);

    keybinds = new Keybindings(intake, hopper, shooter, robotManager, drivetrain);

    registerAutoCommands();

    if (RobotBase.isSimulation()) {
      keybinds.configureNoControllerBindings();
    } else {
      keybinds.configureRealKeybindings();
    }



    
}

  public void registerAutoCommands() {
    NamedCommands.registerCommand("intakeStart",
    robotManager.setState(RobotState.INTAKING)
);

NamedCommands.registerCommand("shoot",
    robotManager.setState(RobotState.SHOOTING_WITH_IAH)
);

NamedCommands.registerCommand("setIdle",
    robotManager.setState(RobotState.IDLE)
);
  }
  

  public Command getAutonomousCommand() {
        return new PathPlannerAuto("depotShoot");
    }  
}
