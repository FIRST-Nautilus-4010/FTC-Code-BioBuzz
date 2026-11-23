package org.firstinspires.ftc.teamcode;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.subsystems.drive.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.shooter.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.utils.Alliance;

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
    private final Set<Mechanism> mechanisms = new HashSet<>(Arrays.asList(drive, intake, shooter));

    public Alliance alliance = Alliance.BLUE;

    public void init(HardwareMap hardwareMap){
        intake.init(hardwareMap);
        shooter.init(hardwareMap);
        drive.init(hardwareMap);
        manager.reset(alliance);
    }

    public void setAlliance(Alliance alliance, Pose startPose){
        this.alliance = alliance;
        drive.setAlliance(alliance,startPose);
        manager.reset(alliance);
    }
    @Override
    public Set<Mechanism> getMechanisms() {
        return mechanisms;
    }

    @Override
    public void periodic(){
        manager.periodic();

        Telemetry.log("State", manager.getCurrentState());
        Telemetry.log("Shooter", String.format("%.0f / %.0f rpm  ready=%b",
                shooter.getRpm(), shooter.getTargetRpm(), shooter.isReady()));
        if (drive.isReady()) {
            Pose p = drive.getPose();
            Telemetry.log("Pose", String.format("x=%.1f y=%.1f h=%.0f°",
                    p.x(), p.y(), Math.toDegrees(p.heading())));
        }
        Telemetry.log("Alliance", alliance);

    }
}
