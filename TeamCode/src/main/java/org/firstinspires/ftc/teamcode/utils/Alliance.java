package org.firstinspires.ftc.teamcode.utils;

import static org.firstinspires.ftc.teamcode.utils.Constants.UNSAFE_ZONE;

import com.pedropathing.math.Pose;
import com.pedropathing.utils.Angle;

public enum Alliance {

    RED,
    BLUE;

    public boolean isRed() { return this == RED; }

    public Pose apply(Pose bluePose) {
        if (!isRed()) return bluePose;
        return new Pose(144 - bluePose.x(), bluePose.y(), mirrorHeading(bluePose.heading()));
    }

    public double mirrorHeading(double blueHeading) {
        return isRed() ? Angle.normalizeSigned(Math.PI - blueHeading) : blueHeading;
    }

    public static Alliance fromPose(Pose pose) {
        double half = 144 / 2;
        if (Math.abs(pose.x() - half) < UNSAFE_ZONE) return null;
        boolean sameSideAsBlueStart = (pose.x() - half) * (FieldPoses.START.x() - half) > 0;
        return sameSideAsBlueStart ? BLUE : RED;
    }


}
