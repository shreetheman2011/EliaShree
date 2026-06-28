package frc.robot.subsystems.vision;


import java.util.ArrayList;
import java.util.List;
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
    private CameraWrapper camera1;
    private CameraWrapper camera2;
    private CameraWrapper[] cameraList;

    // private PhotonPoseEstimator poseEstimator1;

    // private PhotonPoseEstimator poseEstimator2;

    private Optional<EstimatedRobotPose> latestPose1 = Optional.empty();
    private Optional<EstimatedRobotPose> latestPose2 = Optional.empty();


    public Vision() {
        limelight1 = "limelight-joe";
        limelight2 = "limelight-greg";

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

        cameraList[0] = new CameraWrapper(limelight1, robotToCam1);
        cameraList[1] =  new CameraWrapper(limelight2, robotToCam2);
     
    }



    public ArrayList<EstimatedRobotPose> getLatestPoses(){
        ArrayList<EstimatedRobotPose> robotPoses = new ArrayList<>();
        for (CameraWrapper camera: cameraList){
            Optional<EstimatedRobotPose> pose = camera.getVisionMeasurements();

            if(pose.isPresent()){
                robotPoses.add(pose.get());
            }
        }
        return robotPoses;
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