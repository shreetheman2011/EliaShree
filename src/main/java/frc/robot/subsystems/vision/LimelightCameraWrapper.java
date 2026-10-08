package frc.robot.subsystems.vision;

import java.util.Optional;

import edu.wpi.first.math.estimator.PoseEstimator;
import edu.wpi.first.math.geometry.Pose3d;
import limelight.Limelight;
import limelight.networktables.LimelightPoseEstimator.EstimationMode;
import limelight.networktables.LimelightSettings.LEDMode;
import limelight.networktables.LimelightPoseEstimator;
import limelight.networktables.PoseEstimate;

public class LimelightCameraWrapper {
    public Limelight camera;
    public LimelightPoseEstimator visionEstimator;

    public LimelightCameraWrapper(String cameraName){
        this.camera = new Limelight(cameraName);   
        camera.getSettings()
         .withLimelightLEDMode(LEDMode.PipelineControl)
         .withCameraOffset(Pose3d.kZero) //todo: change offset based on where camera actually is
         .save();

        visionEstimator = camera.createPoseEstimator(EstimationMode.MEGATAG1);
    }

    public Optional<PoseEstimate> getPoseEstimate(){
        return visionEstimator.getPoseEstimate();
    }


}
