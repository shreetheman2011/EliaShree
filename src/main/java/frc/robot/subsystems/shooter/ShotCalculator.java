package frc.robot.subsystems.shooter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

import dev.doglog.DogLog;
import edu.wpi.first.math.interpolation.Interpolatable;
import edu.wpi.first.math.interpolation.InterpolatingTreeMap;
import edu.wpi.first.math.util.Units;


public class ShotCalculator {

    //breakdown of everything on the bottom
    public record ShooterData(double rawVelocity, double rawAngle) implements Interpolatable<ShooterData> {


        @Override
        public ShooterData interpolate(ShooterData endValue, double t) {
            double blendedVelocity = this.rawVelocity +t * (endValue.rawVelocity - this.rawVelocity);
            double blendedAngle = this.rawAngle + t * (endValue.rawAngle - this.rawAngle);
            return new ShooterData(blendedVelocity, blendedAngle);
        }

        public double getRPS() {
            return (this.rawVelocity * 7.31) - 11.0;
        }
        

        public double getRotations() {
            return (-0.166667 * this.rawAngle) + 12.0;
        }
    }


    public final InterpolatingTreeMap<Double, ShooterData> targetMap;

    private double minDistance = 1.5; //meters
    private double maxDistance = 6.5; //meters

    public ShotCalculator() {
           this.targetMap = new InterpolatingTreeMap<Double, ShooterData>(
        (upper, lower, query) -> (query - lower) / (upper - lower), // How to calculate the percentage (t) between distances
        (start, end, t) -> start.interpolate(end, t)                // How to blend two ShooterData instances using the record method
    );
        if(!readDatafromResource("Inside.csv")) {
            DogLog.logFault("CSV read failed");
            loadHardcodedFallbackMaps();
        }
    }

    public boolean readDatafromResource(String fileName){
        try (InputStream is = ShotCalculator.class.getResourceAsStream(fileName)){
            if (is == null) return false;

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))){
                String line;
                while((line = br.readLine()) != null){
                    if(line.trim().isEmpty()) continue;

                    String[] values  = line.split(",");
                    if(values.length < 3) continue;

                    double distance = Double.parseDouble(values[0].trim());
                    double velocity = Double.parseDouble(values[1].trim());
                    double angle = Double.parseDouble(values[2].trim());

                    this.targetMap.put(distance, new ShooterData(velocity, angle));

                    this.minDistance = Math.min(this.minDistance, distance);
                    this.maxDistance = Math.max(this.maxDistance, distance);
                }

                DogLog.log("Shot Calculator", "Successfully loaded csv file into target mappings");
                return true;
            }
        } catch (IOException | NumberFormatException e){
            DogLog.log("CSV Fail", e.getMessage());
            return false;
        } 
    }



    private void loadHardcodedFallbackMaps() {
        this.minDistance = Units.inchesToMeters(61.25);
        this.maxDistance = Units.inchesToMeters(196.25);

        this.targetMap.put(this.minDistance, new ShooterData(38, 0));
        this.targetMap.put(Units.inchesToMeters(110.25), new ShooterData(48.0, 1.5));
        this.targetMap.put(this.maxDistance, new ShooterData(60.0, 3.5));

    }


    public ShooterData getShooterSetpoint(double distanceMeters){
        double clampedDistance = Math.max(this.minDistance, Math.min(distanceMeters, this.maxDistance));
        //clamps the distance off so it doesnt map out of bounds

        return Optional.ofNullable(this.targetMap.get(clampedDistance))
                       .orElseGet(() -> new ShooterData(0.0, 0.0));
    
    }
}


// record is a data holder that creates a permanent, but unchanageable way to hold data
//record takes in rawvelocity and rawangle,
//implements Interpolatable<ShooterData> means it can be blended with other data strucutres that have the same ttype


//blending formula:
//this.rawvelocity is the speed setting at our closer tested distance(to where the actual robot is)
//endValue.rawVelocity is the speed setting at the further tested distance(from where the robot is)
//t is a percentage between 0 and 1 supplied by wpilib, if the robot is at 2.5 meters(halfway between 2 and 3 exactly), t will be exactly 0.5
//math: caluclates the differtentce between the two speeds, multiples it by the percentage t, adds it to the starting speed. this kinda complex formula finds the perfect middle ground


//interpolating tree map is a thing that wpilib has that takes key:value pairs and uses them to take middle ground of two values magically


//reads csv file and turns into an array/list idk what its called in java by splitting the rows whereever there is a comma
//parsedouble turns "1.5" into 1.5
//values[0] grabs first item from the chopped list(the distance)

//this.targetmap.put places a new entry in the master map
//distance is the key we are searching with like 1.5
//new shooterdata(velocity, angle) bundles the velocity and anhgle numbers into a new shooterdata pckg and glues it onto that distance. creates a tie between them

//math.min(this.mindistance, distance) compares two numbers and returns the smallest number.
//this is how the code tracks the absolute lowest distance number inside the csv file.

//safety clamp:
//math.max and math.min together forces the number to stay between a min floor and max ceiling

