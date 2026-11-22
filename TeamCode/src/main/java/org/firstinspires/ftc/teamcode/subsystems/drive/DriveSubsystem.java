package org.firstinspires.ftc.teamcode.subsystems.drive;

import static com.pedropathing.ivy.pedro.PedroCommands.hold;


import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.utils.Alliance;
import org.firstinspires.ftc.teamcode.utils.Constants;
import org.firstinspires.ftc.teamcode.utils.FieldPoses;

import dev.nextftc.robot.Mechanism;


public class DriveSubsystem implements Mechanism {

    private Follower follower;

    private double
            forward,
            lateral,
            turn;

    private boolean slow = false;

    private boolean autoOn = false;

    private Pose target = null;
    private Command holdCommand = null;
    private Alliance alliance = Alliance.BLUE;

    public void init (HardwareMap hardwareMap){
        follower = org.firstinspires.ftc.teamcode.pedro.Constants.create(hardwareMap);
        autoOn = false;
        target = null;
        holdCommand = null;

    }

    public void setAlliance(Alliance alliance, Pose starPose){
        this.alliance = alliance;
        follower.setPose(starPose);
    }

    @Override
    public void periodic(){
        if (follower == null) return;

        if (!autoOn){
            double s = slow ? Constants.SLOW_SCALE : 1.0;
            double heading = follower.pose().heading() - driverForward();
            DrivePowers  powers  = ManualDrive.fieldCentric(
                    forward * s, lateral * s, turn * s, heading);
            follower.manual(powers);

        }
        follower.update();
    }

    private double driverForward() {
        return alliance.mirrorHeading(Constants.DRIVER_FORWARD_BLUE_RAD);
    }

    public void setInput(double forward, double lateral, double turn, boolean slow){
        this.forward = forward;
        this.lateral = lateral;
        this.turn = turn;
        this.slow = slow;
    }

    public Command goTo(Pose pose){
        return instant(() -> {
            releaseControl();
            target = pose;
            autoOn = true;
            holdCommand = hold(follower, pose);
            holdCommand.schedule();
        });
    }

    public void releaseControl(){
        autoOn = false;
        if (holdCommand != null ) { holdCommand.cancel(); holdCommand = null;}
    }

    public boolean isAutoOn() { return autoOn; }

    public boolean atTarget() {
        if (!autoOn || target == null) return false;
        Pose cur = follower.pose();
        double dist = Math.hypot(target.x() - cur.x(), target.y() - cur.y());
        double dh = Math.abs(wrap(target.heading() - cur.heading()));
        return dist < Constants.POSE_TOLERANCE_IN
                && dh < Math.toRadians(Constants.HEADING_TOLERANCE_DEG);
    }

    public  Pose getPose() { return follower.pose(); }

    public void resetHeading() {
        Pose pose = follower.pose();
        follower.setPose(new Pose(pose.x(), pose.y(), driverForward()));
    }
    private static double wrap(double a) {
        while (a > Math.PI)  a -= 2 * Math.PI;
        while (a < -Math.PI) a += 2 * Math.PI;
        return a;
    }

}
