package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.subsystems.drive.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.shooter.ShooterSubsystem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import dev.nextftc.robot.Mechanism;
import dev.nextftc.robot.NextRobot;
import dev.nextftc.robot.Telemetry;

public class Robot implements NextRobot {

    public final DriveSubsystem drive = new DriveSubsystem();
    public final IntakeSubsystem intake = new IntakeSubsystem();
    public final ShooterSubsystem shooter = new ShooterSubsystem();
    public final SubsystemManager manager = new SubsystemManager(drive, intake, shooter);

    public void init(HardwareMap hardwareMap){
        drive.init(hardwareMap);
        intake.init(hardwareMap);
        shooter.init(hardwareMap);
        manager.reset();
    }
    @Override
    public Set<Mechanism> getMechanisms() {
        return new HashSet<>(Arrays.asList(drive, intake, shooter));
    }

    @Override
    public void periodic(){
        manager.periodic();

        Telemetry.log("State", manager.getCurrentState());
        Telemetry.log("Shooter", String.format("%.0f / %.0f rpm  ready=%b",
                shooter.getRpm(), shooter.getTargetRpm(), shooter.isReady()));
        Telemetry.log("Pose", String.format("x=%.1f y=%.1f h=%.0f°",
                drive.getPose().x(), drive.getPose().y(), Math.toDegrees(drive.getPose().heading())));

    }
}
