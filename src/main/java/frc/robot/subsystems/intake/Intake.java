package frc.robot.subsystems.intake;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import dev.doglog.DogLog;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {
     private TalonFX intakeMotor;
    private TalonFX pivotMotor;
    private TalonFXConfiguration intakeConfig = new TalonFXConfiguration();
    private TalonFXConfiguration pivotConfig = new TalonFXConfiguration();
    private MotionMagicVoltage motionMagicController = new MotionMagicVoltage(0);
    private IntakeState intakeState;
    
    


    public Intake(){
        intakeMotor = new TalonFX(10);
        pivotMotor = new TalonFX(11);
        configureMotors();
    }

    //very simple method to return a command, makes it nicer to set in keybinds bc you don't need an instant command in the keybinding
    //you can also just put the intakeMotor.setVoltage() in here if you don't want the other method
    public Command setIntakeSpeedCMD(double voltage){
        return new InstantCommand(() -> intakeMotor.setVoltage(voltage));
    }

    //pretty much the same thing as intake speed command just for position, same reason
    public Command setIntakePositionCMD(double position){
        return new InstantCommand(() -> setIntakePosition(position));
    }

    public void setIntakePosition(double position){
        motionMagicController.withPosition(position);
        pivotMotor.setControl(motionMagicController);
        // pivotMotor.setControl(motionMagicController.withPosition(position)); another way to do it so it's just one line, og way should still work
    }

    //this method basically just does what keybinds does so it's a lot easier to do keybinds, just have to call one command to do both
    //you should also make more of these types of methods for other intaking commands (like starting or idle)
    public Command getIntakingCommand(double voltage, double pos){
        return Commands.parallel(
            setIntakeSpeedCMD(voltage),
            setIntakePositionCMD(pos),
            new InstantCommand(() -> DogLog.logFault("Intaking command")) //makes sure that it runs, logging faults is good b/c it just gives you a count to make sure it runs
        ).withName("Intaking Command"); //the with name helps you keep track of the commands being run, helps debug
    }


    public Command stopIntakingCommand(double voltage, double pos){
        return Commands.parallel(
            setIntakeSpeedCMD(voltage),
            setIntakePositionCMD(pos),
            new InstantCommand(() -> DogLog.logFault("Intake stopping command")) 

        ).withName("Intake stopping command");
    }

    //if you want, you can store the configs in another file with static methods. very much optional but it can make each subsystem a bit cleaner
    public void configureMotors(){
        intakeConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        intakeConfig.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;

        intakeConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        intakeConfig.CurrentLimits.SupplyCurrentLimitEnable = true;
        intakeConfig.CurrentLimits.SupplyCurrentLimit = 120;
        intakeConfig.CurrentLimits.StatorCurrentLimit = 120;

        intakeMotor.getConfigurator().apply(intakeConfig);

        

        /* this is like pretty much optional to do, it's just something that i made to ensure that the config applies every time b/c there can sometimes
            be issues with it
            it pretty much just checks the status code to make sure that its not failed and if it stays failed after 5 tries it ends it

        int intakeCount = 0;

        while(intakeMotor.getConfigurator().refresh(intakeConfig).equals(StatusCode.ConfigFailed) && intakeCount <=5){
            intakeMotor.getConfigurator().apply(intakeConfig);
            DogLog.logFault("Motor " + intakeMotor.getDeviceID() +  " Config Failed");
            intakeCount++;
        }
            */

        pivotConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        pivotConfig.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;

        pivotConfig.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
        pivotConfig.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;
        pivotConfig.SoftwareLimitSwitch.ForwardSoftLimitThreshold = 8;
        pivotConfig.SoftwareLimitSwitch.ReverseSoftLimitThreshold = 0;


        pivotConfig.Slot0.kP = 0.9;
        pivotConfig.Slot0.kI = 0;
        pivotConfig.Slot0.kD = 0.1;
        pivotConfig.Slot0.kS = 1.3;
        pivotConfig.Slot0.kV = 0;
        pivotConfig.Slot0.kA = 0;

        pivotConfig.MotionMagic.MotionMagicAcceleration = 30;
        pivotConfig.MotionMagic.MotionMagicCruiseVelocity = 30;
        pivotConfig.Feedback.FeedbackRotorOffset = 0.04703126275;

        pivotConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        pivotConfig.CurrentLimits.StatorCurrentLimit = 40;
        pivotConfig.CurrentLimits.SupplyCurrentLimitEnable = true;
        pivotConfig.CurrentLimits.SupplyCurrentLimit = 40;

        pivotMotor.getConfigurator().apply(pivotConfig);
    }


    public Command setState (IntakeState state){
        this.intakeState = state;
        switch (state) {
            case INTAKING:
                return getIntakingCommand(state.getVoltage(), state.getPos());
            case IDLE:
                return stopIntakingCommand(state.getVoltage(), state.getPos());
            default:
                return stopIntakingCommand(0, 0);
        }
    }


    // public Command getStateCommand(IntakeState state){
    //     switch (state){
    //         case INTAKING:
    //             return this.getIntakingCommand().withName("Running Intake");
    //         default:
    //             return this.stopIntakingCommand();
    //     }
    // }

   

    //example of how to do some logging, you'll probably want to log more stuff but this is a good start, lets you keep track of the stuff you apply to the motors
    //logging physical stuff on the motors like i did below isn't great for debugging in sim
    //it's a lot easier to use sim to debug when you have state machines b/c you can just log the state and the applied commands
    @Override
    public void periodic(){
        DogLog.log("Intake Voltage", intakeMotor.getMotorVoltage().getValueAsDouble());
        DogLog.log("Intake Pivot Position", pivotMotor.getPosition().getValueAsDouble());
        DogLog.log("Intake state", intakeState);
    }
}
