package frc.robot.subsystems.hopper;

public enum HopperState {
    HOPPING(5),
    IDLE(0);

    private double voltage;


    private HopperState(double voltage){
        this.voltage = voltage;
    }

    public double getVoltage(){
        return voltage;
    }


}
