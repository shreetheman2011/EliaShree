package frc.robot;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.RadiansPerSecond;
import static edu.wpi.first.units.Units.RotationsPerSecond;

import com.ctre.phoenix6.swerve.SwerveRequest;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;

import dev.doglog.DogLog;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.hopper.Hopper;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.swerve.CommandSwerveDrivetrain;

public class Keybindings {

    private CommandXboxController controller;
    private Intake intake;
    private Hopper hopper;
    private Shooter shooter;
    private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond);
    private double MaxAngularRate = RotationsPerSecond.of(1.2).in(RadiansPerSecond);

    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
    .withDeadband(MaxSpeed * 0.2).withRotationalDeadband(MaxAngularRate * 0.2)
    .withDriveRequestType(DriveRequestType.OpenLoopVoltage);

    private final Telemetry logger = new Telemetry(MaxSpeed);

    private final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();

    public Keybindings(){
        controller = new CommandXboxController(0);
        intake = new Intake();
        hopper = new Hopper(); 
        shooter = new Shooter();
        //the way i've done it in the past, i created the new subsystems in robotContainer and passed it in to keybinds, but this works too
        //i prefer robot container bc when i add state machines i define the robotManager in robot container so it's easier
        //whenever you make a new subsystem always make sure that's the only new one you make cuz it wont work if you have multiple (you have a intake in robot container)
        //idk where to put this but your code doesnt crash in sim so good job
    }

    /*
    if you want you can use this to pass in subsystems if you wanna make them in robotContainer

    public Keybindings(Intake intake, Hopper hopper, Shooter shooter){
        this.intake = intake;
        this.hopper = hopper;
        this.shooter = shooter;
    }

    */
    public void configureRealKeybindings(){



        //drivetrain stuff

        drivetrain.setDefaultCommand(
            //executed periodically
            drivetrain.applyRequest(() -> 
                drive.withVelocityX(-controller.getLeftY() * MaxSpeed)
                .withVelocityY(-controller.getLeftX() * MaxSpeed)
                .withRotationalRate(-controller.getRightX() * MaxAngularRate *.5)
            )
        );

        

        drivetrain.registerTelemetry(logger::telemeterize);




        
        controller.leftTrigger().onTrue(intake.getIntakingCommand()).onFalse(intake.stopIntakingCommand());

        
         
        // controller.leftBumper().onTrue(new InstantCommand(() -> DogLog.logFault("Left Bumper Press"))); look i used this to debug something

        // controller.leftBumper().onTrue(intake.getIntakingCommand())
        //    .onFalse(intake.getIdleCommand());
         /* 
         * example of how to do keybinds with nicer commands, idle command doesnt exist yet but you get the idea
         * also you can just do .onFalse attatched to the onTrue to make it a little cleaner
         * 
         * i would recommend you change whileTrue to onTrue, they're pretty much the same but we use onTrue more so i trust it more
         */


        controller.b().onTrue(hopper.getHoppingCommand()).onFalse(hopper.stopHoppingCommand());

    

        controller.rightBumper().onTrue(shooter.getShootingCMD()).onFalse(shooter.stopShootingCMD());



        controller.rightTrigger().onTrue(
            Commands.sequence(
                shooter.getShootingCMD(),
                Commands.waitSeconds(0.5),
                Commands.parallel(

                    hopper.getHoppingCommand(),
                    intake.getIntakingCommand()
                )
            )
        ).onFalse( Commands.parallel(
                shooter.stopShootingCMD(),
                intake.stopIntakingCommand(),
                hopper.stopHoppingCommand()
        ));
}}
