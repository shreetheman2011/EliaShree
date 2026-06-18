package frc.robot.subsystems.shooter;


import java.util.Optional;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import dev.doglog.DogLog;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.shooter.ShotCalculator.ShooterData;
import frc.robot.subsystems.vision.Vision;
import frc.robot.subsystems.vision.Vision.VisionMeasurement;

public class Shooter extends SubsystemBase {
    private TalonFX shooterMotorLeft;
    private TalonFX shooterMotorRight;
    private TalonFX angleMotor;
    private TalonFX feederMotor;

    private MotionMagicVoltage motionMagic;

    private TalonFXConfiguration angleMotorConfig;
    private TalonFXConfiguration shooterMotorsConfig;
    private TalonFXConfiguration feederMotorConfig;
    private ShooterState shooterState;
    private Vision vision;
    


    private ShotCalculator calculator = new ShotCalculator();




    public Shooter(Vision vision){
        shooterMotorLeft=new TalonFX(15);
        shooterMotorRight = new TalonFX(16);
        angleMotor = new TalonFX(14);
        feederMotor = new TalonFX(13);
        this.vision = vision;

        motionMagic = new MotionMagicVoltage(0);
        angleMotorConfig = new TalonFXConfiguration();
        shooterMotorsConfig = new TalonFXConfiguration();
        feederMotorConfig = new TalonFXConfiguration();

        configureMotors();

    }

    public void setVoltage(double voltage){
        shooterMotorLeft.setVoltage(voltage);
    }
    //shooter motor right doesnt need to be called because it is set as a follower motor in configuremotors

    public void setAngle(double pos){
        motionMagic.withPosition(pos);
        angleMotor.setControl(motionMagic);
    }



    public Command setVoltageCMD(double voltage){
        return new InstantCommand(() -> setVoltage(voltage));
    }

    

    //replaced instant command and stuff in here to make it so that everything waits to shoot until the angle is reached
    public Command setAngleCMD(double pos) {
        return Commands.runOnce(() -> setAngle(pos))
        .andThen(
            Commands.waitUntil(
                () -> Math.abs(
                    angleMotor.getPosition().getValueAsDouble() - pos
                ) < 0.05
            )
        );
}

    public Command spinFeederCMD(double voltage){
        return new InstantCommand(() ->feederMotor.setVoltage(voltage));
    }




    public Command getShootingCMD (){
        return Commands.sequence(
            setAngleCMD(0.5), //todo: change angle, feeder, and shooter main motor voltages 
            setVoltageCMD(5),
            Commands.waitSeconds(0.5),
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



    public Command getAutoShootingCMD(){
        return Commands.sequence(
            Commands.runOnce(() -> {
                VisionMeasurement measurement = this.vision.getVisionMeasurement();
                double distanceMeters = 2.5;

                if(measurement !=null && measurement.pose != null){
                    try {
                    AprilTagFieldLayout fieldLayout = AprilTagFields.k2026RebuiltAndymark.loadAprilTagLayoutField();

                    Optional<Alliance> alliance = DriverStation.getAlliance();

                    int hubTargetTagID = 25;
                    if(alliance.isPresent() && alliance.get() == Alliance.Red ){
                        hubTargetTagID = 10;
                    }

                    Optional<Pose3d> targetPose = fieldLayout.getTagPose(hubTargetTagID);
                

                    if (targetPose.isPresent()) {
                        Pose2d hubTarget = targetPose.get().toPose2d();
                        distanceMeters = measurement.pose.getTranslation().getDistance(hubTarget.getTranslation());
                    }
                    } catch (Exception e){
                        distanceMeters = 2.5;
                    }
                }

                ShooterData setpoint = calculator.getShooterSetpoint(distanceMeters);

                double targetRotations = setpoint.getRotations();
                double targetRPS = setpoint.getRPS();

                double targetVoltage = (targetRPS / 80.0) * 12.0;

                setAngle(targetRotations);
                setVoltage(targetVoltage);
            }),

        

            //wait for angle to reach

            Commands.waitUntil(() -> {
                VisionMeasurement measurement = this.vision.getVisionMeasurement();
                double checkDistance = 2.5;

                if(measurement != null && measurement.pose !=null) {
                    try {
                        AprilTagFieldLayout layout = AprilTagFields.k2026RebuiltAndymark.loadAprilTagLayoutField();
                        int tagId = (DriverStation.getAlliance().orElse(Alliance.Blue) == Alliance.Red) ? 10 : 25;
                        checkDistance = layout.getTagPose(tagId).map(p -> measurement.pose.getTranslation().getDistance(p.toPose2d().getTranslation())).orElse(2.5);

                    } catch(Exception e) {};
                }

                return Math.abs(angleMotor.getPosition().getValueAsDouble() - calculator.getShooterSetpoint(checkDistance).getRotations()) < 0.05;
            }),



            Commands.waitSeconds(0.5),

            spinFeederCMD(5),
            new InstantCommand(() -> DogLog.logFault("Auto shooter"))


        ).withName("Auto shooting command");
    }




    public Command setState(ShooterState state){
        this.shooterState = state;
        switch(state) {
            case SHOOTING:
                return getShootingCMD();
            case IDLE:
                return stopShootingCMD();
            case AUTO_SHOOTING:
                return getAutoShootingCMD();
            default:
                return stopShootingCMD();
        }
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

        shooterMotorsConfig.TorqueCurrent.PeakForwardTorqueCurrent = .0; //todo: check this
        shooterMotorsConfig.TorqueCurrent.PeakReverseTorqueCurrent = -40.0;
        shooterMotorsConfig.MotorOutput.PeakForwardDutyCycle = 0.0; //todo: check this
        shooterMotorsConfig.MotorOutput.PeakReverseDutyCycle = -1.0;

        feederMotorConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;


        feederMotor.getConfigurator().apply(feederMotorConfig);

        shooterMotorLeft.getConfigurator().apply(shooterMotorsConfig);
        shooterMotorRight.getConfigurator().apply(shooterMotorsConfig);

        shooterMotorRight.setControl(new Follower(this.shooterMotorLeft.getDeviceID(), MotorAlignmentValue.Opposed));


    }

    @Override
    public void periodic(){
        DogLog.log("Shooter state", shooterState);
    }
}
