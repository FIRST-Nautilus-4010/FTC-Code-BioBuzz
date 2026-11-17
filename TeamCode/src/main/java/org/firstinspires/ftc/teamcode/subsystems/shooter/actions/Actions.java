package org.firstinspires.ftc.teamcode.subsystems.shooter.actions;

import com.pedropathing.ivy.Command;

import org.firstinspires.ftc.teamcode.subsystems.shooter.ShooterSubsystem;

import dev.nextftc.robot.Mechanism;

public class Actions {

    private Actions(){}

    public static Command shoot(Mechanism owner, double rpm) {
        ShooterSubsystem subsystem = (ShooterSubsystem) owner;
        return owner.instant(() -> subsystem.setTargetRpm(rpm));
    }

    public static Command stop(Mechanism owner) {
        ShooterSubsystem subsystem = (ShooterSubsystem) owner;
        return owner.instant(subsystem::halt);
    }

}
