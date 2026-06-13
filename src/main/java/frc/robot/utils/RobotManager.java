package frc.robot.utils;

import dev.doglog.DogLog;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.hopper.Hopper;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.shooter.Shooter;

public class RobotManager extends SubsystemBase {
    private RobotState robotState;
    private Intake intake;
    private Hopper hopper;
    private Shooter shooter;

    public RobotManager(Intake intake, Hopper hopper, Shooter shooter){
        robotState = RobotState.IDLE;
        this.intake = intake;
        this.hopper = hopper;
        this.shooter = shooter;
    };

    public Command setState(RobotState state){
        return Commands.parallel(
            intake.setState(state.getIntakeState()),
            hopper.setState(state.getHopperState()),
            shooter.setState(state.getShooterState()),
            new InstantCommand(() -> this.robotState = state)

        );
    }

    @Override
    public void periodic(){
        DogLog.log("Robot state", robotState);
        DogLog.log("Intake state from robot manager", robotState.getIntakeState());
        DogLog.log("Hopper state from robot manager", robotState.getHopperState());
        DogLog.log("Shooter state from robot manager", robotState.getShooterState());
    }
}
