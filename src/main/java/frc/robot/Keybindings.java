package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.hopper.Hopper;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.shooter.Shooter;

public class Keybindings {

    private CommandXboxController controller;
    private Intake intake;
    private Hopper hopper;
    private Shooter shooter;

    public Keybindings(){
        controller = new CommandXboxController(0);
        intake = new Intake();
        hopper = new Hopper(); 
        shooter = new Shooter();
    }
    public void configureKeybindings(){
        
        controller.leftTrigger().whileTrue(new InstantCommand(() -> {
            intake.setIntakePosition(0);
            intake.setIntakeSpeed(0);
        })); 

        controller.leftTrigger().onFalse(new InstantCommand(() -> {
            intake.setIntakePosition(0);
            intake.setIntakeSpeed(0);
        }));    


        controller.b().whileTrue(new InstantCommand(()->{
            hopper.setVoltage(0);
        }));

        controller.b().onFalse(new InstantCommand(()->{
            hopper.setVoltage(0);
        }));


        controller.rightBumper().whileTrue(new InstantCommand(()-> {
            shooter.setVoltage(0);
            shooter.setAngle(0);
            shooter.spinFeeder(0);


        }));

        controller.rightBumper().onFalse(new InstantCommand(()-> {
            shooter.setVoltage(0);
            shooter.setAngle(0);
            shooter.spinFeeder(0);

        }));



        controller.rightTrigger().whileTrue(new InstantCommand(() -> {
            hopper.setVoltage(0);
            shooter.setVoltage(0);
            shooter.setAngle(0);
            shooter.spinFeeder(0);
        }));




}
}
