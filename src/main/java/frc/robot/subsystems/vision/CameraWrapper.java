package frc.robot.subsystems.vision;

import java.util.Optional;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Transform3d;

public class CameraWrapper {
    public final PhotonCamera camera;
    private PhotonPoseEstimator poseEstimator;
    private Transform3d robotToCam;

    AprilTagFieldLayout fieldLayout; 



    public CameraWrapper(String camera, Transform3d robotToCam){
        this.camera = new PhotonCamera(camera);
        this.robotToCam = robotToCam;
        fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2026RebuiltAndymark);

        poseEstimator = new PhotonPoseEstimator(fieldLayout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, robotToCam);

    }
    

    public Optional<EstimatedRobotPose> getVisionMeasurements(){
        Optional<EstimatedRobotPose> newestEstimate = Optional.empty();

        for (PhotonPipelineResult result: camera.getAllUnreadResults()){
            if (!result.hasTargets()){
                continue;
            }

            if(result.getTargets().size() == 1 && result.getBestTarget().getPoseAmbiguity() > 0.2){
                continue;
            }

            newestEstimate = poseEstimator.estimateCoprocMultiTagPose(result);
        }
        
        return newestEstimate;

    }

    public void setDriverMode(boolean driverMode){
        camera.setDriverMode(driverMode);
    }
}
