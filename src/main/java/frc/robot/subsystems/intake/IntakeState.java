package frc.robot.subsystems.intake;

public enum IntakeState {
    INTAKING(5, 7.1),
    IDLE(0, 0);

    private double voltage;

    private double position;

    private IntakeState(double voltage, double position){
        this.voltage = voltage;
        this.position = position;
    }


    public double getVoltage(){
        return voltage;
    }

    public double getPos(){
        return position;
    }
}
