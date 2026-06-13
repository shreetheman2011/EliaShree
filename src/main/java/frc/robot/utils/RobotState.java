package frc.robot.utils;

import frc.robot.subsystems.intake.IntakeState;
import frc.robot.subsystems.shooter.ShooterState;
import frc.robot.subsystems.hopper.HopperState;

public enum RobotState {
    INTAKING(IntakeState.INTAKING, HopperState.IDLE, ShooterState.IDLE),
    IDLE(IntakeState.IDLE, HopperState.IDLE, ShooterState.IDLE),
    HOPPING(IntakeState.IDLE, HopperState.HOPPING, ShooterState.IDLE),
    JUST_SHOOTING(IntakeState.IDLE, HopperState.IDLE, ShooterState.SHOOTING),
    SHOOTING_WITH_IAH(IntakeState.INTAKING, HopperState.HOPPING, ShooterState.SHOOTING); 
    //IAH = intake and hopper


    private IntakeState intakeState;
    private HopperState hopperState;
    private ShooterState shooterState;

    private RobotState(IntakeState intakeState, HopperState hopperState, ShooterState shooterState){
        this.intakeState = intakeState;
        this.hopperState = hopperState;
        this.shooterState = shooterState;
    }


    public IntakeState getIntakeState(){
        return intakeState;
    }

    public HopperState getHopperState(){
        return hopperState;
    }

    public ShooterState getShooterState(){
        return shooterState;
    }


}
