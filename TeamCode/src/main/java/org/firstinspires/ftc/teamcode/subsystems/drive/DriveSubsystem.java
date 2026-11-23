package org.firstinspires.ftc.teamcode.subsystems.drive;

import static com.pedropathing.ivy.pedro.PedroCommands.hold;


import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.utils.Angle;
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

    private double headingOffset = 0;


    public void init (HardwareMap hardwareMap){
        follower = org.firstinspires.ftc.teamcode.pedro.Constants.create(hardwareMap);
        forward = lateral = turn = 0;
        slow = false;
        autoOn = false;
        target = null;
        holdCommand = null;
        headingOffset = 0;
    }

    public boolean isReady() { return follower != null; }
    public void setAlliance(Alliance alliance, Pose startPose){
        this.alliance = alliance;
        headingOffset = 0;
        if (follower != null) follower.setPose(startPose);
    }

    @Override
    public void periodic(){
        if (follower == null) return;

        if (!autoOn){
            double s = slow ? Constants.SLOW_SCALE : 1.0;
            double heading = follower.pose().heading() - driverForward() - headingOffset;
            DrivePowers  powers  = ManualDrive.fieldCentric(
                    forward * s, lateral * s, turn * s, heading);
            follower.manual(powers);

        }
        follower.update();
    }

    private double driverForward() {
        return alliance.rotateHeading(Constants.DRIVER_FORWARD_BLUE_RAD);
    }

    public void setInput(double forward, double lateral, double turn, boolean slow){
        this.forward = forward;
        this.lateral = lateral;
        this.turn = turn;
        this.slow = slow;
    }

    public void setTarget(Pose pose){
        if (follower == null) return;
        releaseControl();
        target = pose;
        autoOn = true;
        holdCommand = hold(follower, pose);
        holdCommand.schedule();
    }

    public Command goTo(Pose pose){
        return instant(() -> setTarget(pose));
    }

    public void releaseControl(){
        autoOn = false;
        if (holdCommand != null) { holdCommand.cancel(); holdCommand = null; }
    }

    public boolean isAutoOn() { return autoOn; }

    public boolean atTarget() {
        if (follower == null || !autoOn || target == null) return false;
        Pose cur = follower.pose();
        double dist = Math.hypot(target.x() - cur.x(), target.y() - cur.y());
        double dh = Math.abs(Angle.normalizeSigned(target.heading() - cur.heading()));
        return dist < Constants.POSE_TOLERANCE_IN
                && dh < Math.toRadians(Constants.HEADING_TOLERANCE_DEG);
    }

    public Pose getPose() {
        return follower == null ? new Pose(0, 0, 0) : follower.pose();
    }

    public void resetHeading() {
        if (follower == null) return;
        headingOffset = Angle.normalizeSigned(follower.pose().heading() - driverForward());
    }
}
