package frc.robot.subsystems.hopper;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VelocityDutyCycle;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import dev.doglog.DogLog;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Hopper extends SubsystemBase {
    private TalonFX motor = new TalonFX(12);

    public Hopper(){
        configureMotors();
    }

    public void periodic(){
        DogLog.log("Hopper Voltage", motor.getMotorVoltage().getValueAsDouble());
    }

    public Command setVoltageCMD(double voltage){
        return new InstantCommand(() -> motor.setVoltage(voltage));
    };


      public Command getHoppingCommand (){
        return Commands.parallel(
            setVoltageCMD(5), //todo change voltage
            new InstantCommand(() -> DogLog.logFault("Hopping command")));
    }

      public Command stopHoppingCommand (){
        return Commands.parallel(
            setVoltageCMD(0),
            new InstantCommand(() -> DogLog.logFault("Stop hopping command")) 
        );
    }




    public void configureMotors(){
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        config.SoftwareLimitSwitch.ForwardSoftLimitEnable = false;
        config.SoftwareLimitSwitch.ReverseSoftLimitEnable = false;

        config.CurrentLimits.StatorCurrentLimitEnable = true;
        config.CurrentLimits.StatorCurrentLimit = 30;
        config.CurrentLimits.SupplyCurrentLimitEnable = true;
        config.CurrentLimits.SupplyCurrentLimit = 20;

        motor.getConfigurator().apply(config);
    }
}
