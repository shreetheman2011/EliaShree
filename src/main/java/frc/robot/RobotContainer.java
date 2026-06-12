// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.intake.Intake;

public class RobotContainer {
  private CommandXboxController controller;
  private Intake intake;
  private Keybindings keybinds;
  public RobotContainer() {
    controller = new CommandXboxController(0);
    intake = new Intake();//this is bad cuz you already made a new intake in keybindings
    //pick one bruh either this or the one in keybindings

    /*
     * intake = new Intake();
     * hopper = new Hopper();
     * shooter = new Shooter();
     * 
     * keybindings = new Keybindings(intake, hopper, shooter);
     * 
     * this is how you would make the subsystems in container and pass into keybinds
     */

    keybinds = new Keybindings();

    keybinds.configureRealKeybindings();
  }

  

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
