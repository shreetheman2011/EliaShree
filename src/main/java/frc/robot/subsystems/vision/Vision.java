package frc.robot.subsystems.vision;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.vision.LimelightHelpers.PoseEstimate;

public class Vision extends SubsystemBase {
    public String limelight1;
    public String limelight2;


    public Vision(){
       limelight1 = "limelight-joe";
       limelight2 = "limelight-greg";
    }

    //vision measurement class to combine pose and timestamp
    public class VisionMeasurement {
        public Pose2d pose;
        public double timestamp;

        public VisionMeasurement(Pose2d pose, double timestamp) {
            this.pose = pose;
            this.timestamp = timestamp; 
        }
    };

    public VisionMeasurement getVisionMeasurement() {
        PoseEstimate limelight1Pose = LimelightHelpers.getBotPoseEstimate_wpiBlue(limelight1);
        PoseEstimate limelight2Pose = LimelightHelpers.getBotPoseEstimate_wpiBlue(limelight2);
        Pose2d averageOfPoses;

        //for testing:
        // limelight2Pose = new PoseEstimate();
        // limelight2Pose.tagCount = 2;
        // limelight2Pose.avgTagDist = 4;
        // limelight1Pose = new PoseEstimate();
        // limelight1Pose.tagCount = 2;
        // limelight1Pose.avgTagDist = 4;

        if(limelight1Pose != null && limelight2Pose != null){
            averageOfPoses = new Pose2d(  (limelight1Pose.pose.getX() + limelight2Pose.pose.getX()) / 2,   (limelight1Pose.pose.getY() + limelight2Pose.pose.getY()) / 2,   (new Rotation2d((limelight1Pose.pose.getRotation().getRadians() + limelight2Pose.pose.getRotation().getRadians()) /2))  );
        } else {
            averageOfPoses= Pose2d.kZero;
        }
 



        //check if each cam has 2 tags at least in sight
        //if so, avg
        if (limelight1Pose != null && limelight2Pose != null && limelight1Pose.tagCount >= 2 && limelight2Pose.tagCount >= 2 && limelight1Pose.avgTagDist < 5 && limelight2Pose.avgTagDist < 5 ){
            return new VisionMeasurement(averageOfPoses, ((limelight1Pose.timestampSeconds + limelight2Pose.timestampSeconds) / 2)); 

        } else if (limelight2Pose != null && limelight2Pose.tagCount >=2 && limelight2Pose.avgTagDist < 5 ){
            //check if limelight2 sees more than 2 tags but limelight 1 doesnt
            
            return new VisionMeasurement(limelight2Pose.pose, limelight2Pose.timestampSeconds); 

        
        } else if (limelight1Pose != null && limelight1Pose.tagCount >=2 && limelight1Pose.avgTagDist < 5 ) {
            //check if limelight 1 sees more than 2 tags but limelight 2 doesnt
            return new VisionMeasurement(limelight1Pose.pose, limelight1Pose.timestampSeconds); 

        } else {
            return null;
        }



    };

}
