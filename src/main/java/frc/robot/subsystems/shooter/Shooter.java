package frc.robot.subsystems.shooter;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import dev.doglog.DogLog;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {
    private TalonFX shooterMotorLeft;
    private TalonFX shooterMotorRight;
    private TalonFX angleMotor;
    private TalonFX feederMotor;

    private MotionMagicVoltage motionMagic;

    private TalonFXConfiguration angleMotorConfig;
    private TalonFXConfiguration shooterMotorsConfig;
    private TalonFXConfiguration feederMotorConfig;


    public Shooter(){
        shooterMotorLeft=new TalonFX(15);
        shooterMotorRight = new TalonFX(16);
        angleMotor = new TalonFX(14);
        feederMotor = new TalonFX(13);

        motionMagic = new MotionMagicVoltage(0);
        angleMotorConfig = new TalonFXConfiguration();
        shooterMotorsConfig = new TalonFXConfiguration();
        feederMotorConfig = new TalonFXConfiguration();

        configureMotors();

    }

    public void setVoltage(double voltage){
        shooterMotorRight.setControl(new Follower(this.shooterMotorLeft.getDeviceID(), MotorAlignmentValue.Opposed));
        shooterMotorLeft.setVoltage(voltage);
    }

    public void setAngle(double pos){
        motionMagic.withPosition(pos);
        angleMotor.setControl(motionMagic);
    }



    public Command setVoltageCMD(double voltage){
        return new InstantCommand(() -> setVoltage(voltage));
    }

    public Command setAngleCMD(double pos){
        return new InstantCommand(() -> setAngle(pos));
    }

    public Command spinFeederCMD(double voltage){
        return new InstantCommand(() ->feederMotor.setVoltage(voltage));
    }




    public Command getShootingCMD (){
        return Commands.parallel(
            setVoltageCMD(5),
            setAngleCMD(1), //todo: change angle, feeder, and shooter main motor voltages 
            spinFeederCMD(5),
            new InstantCommand(() -> DogLog.logFault("Shooter command"))).withName("Get shooting command");
    }

    public Command stopShootingCMD (){
        return Commands.parallel(
            setVoltageCMD(0),
            setAngleCMD(0), 
            spinFeederCMD(0),
            new InstantCommand(() -> DogLog.logFault("Stop shooting command"))).withName("Stop Shooting Command");
    }




    public void configureMotors() {
        angleMotorConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        angleMotorConfig.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        angleMotorConfig.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
        angleMotorConfig.SoftwareLimitSwitch.ForwardSoftLimitThreshold = 5.33;
        angleMotorConfig.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;
        angleMotorConfig.SoftwareLimitSwitch.ReverseSoftLimitThreshold = 0;

        angleMotorConfig.MotionMagic.MotionMagicAcceleration = 50;
        angleMotorConfig.MotionMagic.MotionMagicCruiseVelocity = 50;
        angleMotorConfig.Feedback.FeedbackRotorOffset = -0.271973;

        angleMotor.getConfigurator().apply(angleMotorConfig);

        shooterMotorsConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;


        shooterMotorsConfig.MotorOutput.withInverted(InvertedValue.CounterClockwise_Positive);

        shooterMotorsConfig.TorqueCurrent.PeakForwardTorqueCurrent = .0;
        shooterMotorsConfig.TorqueCurrent.PeakReverseTorqueCurrent = -40.0;
        shooterMotorsConfig.MotorOutput.PeakForwardDutyCycle = 0.0;
        shooterMotorsConfig.MotorOutput.PeakReverseDutyCycle = -1.0;

        feederMotorConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;


        feederMotor.getConfigurator().apply(feederMotorConfig);

        shooterMotorLeft.getConfigurator().apply(shooterMotorsConfig);
        shooterMotorRight.getConfigurator().apply(shooterMotorsConfig);

    }
}
