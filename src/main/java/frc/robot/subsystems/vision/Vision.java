package frc.robot.subsystems.vision;


import java.util.Optional;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Vision extends SubsystemBase {
    public String limelight1;
    public String limelight2;
    private PhotonCamera camera1;
    private PhotonCamera camera2;

    private PhotonPoseEstimator poseEstimator1;

    private PhotonPoseEstimator poseEstimator2;


    public Vision() {
        limelight1 = "limelight-joe";
        limelight2 = "limelight-greg";

        camera1 = new PhotonCamera(limelight1);
        camera2 = new PhotonCamera(limelight2);

        AprilTagFieldLayout fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2026RebuiltAndymark);


        //need to change these
        Transform3d robotToCam1 = new Transform3d(
            Units.inchesToMeters(0),
            Units.inchesToMeters(0),
            Units.inchesToMeters(0),
            new Rotation3d(
                0,
                Math.toRadians(0),
                0
            )
        );

        Transform3d robotToCam2 = new Transform3d(
            Units.inchesToMeters(0),
            Units.inchesToMeters(0),
            Units.inchesToMeters(0),
            new Rotation3d(
                0,
                Math.toRadians(0),
                0
            )
        );

        poseEstimator1 = new PhotonPoseEstimator(
            fieldLayout,
            PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR,
            robotToCam1
        );

        poseEstimator2 = new PhotonPoseEstimator(
            fieldLayout,
            PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR,
            robotToCam2
        );
    }

    public Optional<EstimatedRobotPose> getEstimatedPose1() {
        PhotonPipelineResult result = camera1.getLatestResult();
        if (!result.hasTargets()) {
            return Optional.empty();
        } else if (result.getBestTarget().getPoseAmbiguity() > 0.2) {
            return Optional.empty();
        }
        return poseEstimator1.update(result);
    }

    public Optional<EstimatedRobotPose> getEstimatedPose2() {
        PhotonPipelineResult result = camera2.getLatestResult();
                if (!result.hasTargets()) {
            return Optional.empty();
        } else if (result.getTargets().size() < 2) {
            return Optional.empty();
        } else if (result.getBestTarget().getPoseAmbiguity() > 0.2) {
            return Optional.empty();
        }
        return poseEstimator2.update(result);
    }

    public void turnOffCams(){
        camera1.setDriverMode(true);
        camera2.setDriverMode(true);
        
    }
    public void turnOnCams(){
        camera1.setDriverMode(false);
        camera2.setDriverMode(false);

    }

}