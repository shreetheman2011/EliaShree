package frc.robot.subsystems.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.hopper.Hopper;
import frc.robot.subsystems.intake.Intake;
import frc.robot.subsystems.shooter.Shooter;
import frc.robot.subsystems.shooter.ShotCalculator;
import frc.robot.subsystems.shooter.ShotCalculator.ShooterData;
import frc.robot.subsystems.swerve.CommandSwerveDrivetrain;
import frc.robot.subsystems.vision.Vision;

public class MultiSub {

    public MultiSub() {

    }

    // static means I can call it like MultiSub.autoShootCmd instead of making a new
    // instance!! :D
    // also next time make it so that I don't have to directly call shot calculator
    // as a parameter for the autoShootCmd. Nate told me this! On 10/13/2026 at 4:06
    // pm. For record keeping and memories!!! :D:D :D :D :D

    /*
     * grab data from the shotcalculator
     * then, spin up shooter by calling setVoltageCMD to the necessary voltage based
     * on what shot calculator gave, then Commands.waitSeconds()
     * then, call spinFeederCMD, then call
     */

    public static Command autoShootCmd(Intake intake, Hopper hopper, Shooter shooter, Vision vision,
            CommandSwerveDrivetrain driveTrain, ShotCalculator shotCalculator) {
        return Commands.sequence(
                shooter.setVoltageWithDistancePIDCMD(() -> driveTrain.getDistanceToHub()));
    }

}
