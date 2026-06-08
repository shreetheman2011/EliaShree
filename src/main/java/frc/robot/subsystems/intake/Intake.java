package frc.robot.subsystems.intake;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {
     private TalonFX intakeMotor;
    private TalonFX pivotMotor;
    private TalonFXConfiguration intakeConfig = new TalonFXConfiguration();
    private TalonFXConfiguration pivotConfig = new TalonFXConfiguration();
    private MotionMagicVoltage motionMagicController = new MotionMagicVoltage(0);
    


    public Intake(){
        intakeMotor = new TalonFX(10);
        pivotMotor= new TalonFX(11);
        configureMotors();
    }


    public void setIntakeSpeed(double speed){
        intakeMotor.set(speed);
    }

    public void setIntakePosition(double position){
        motionMagicController.withPosition(position);
        pivotMotor.setControl(motionMagicController);
    }



    public void configureMotors(){
        intakeConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        intakeConfig.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;

        intakeConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        intakeConfig.CurrentLimits.SupplyCurrentLimitEnable = true;
        intakeConfig.CurrentLimits.SupplyCurrentLimit = 120;
        intakeConfig.CurrentLimits.StatorCurrentLimit = 120;

        intakeMotor.getConfigurator().apply(intakeConfig);

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
}
