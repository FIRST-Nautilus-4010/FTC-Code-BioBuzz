package org.firstinspires.ftc.teamcode.utils;

import static org.firstinspires.ftc.teamcode.utils.Constants.UNSAFE_ZONE;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.utils.Angle;

public enum Alliance {
    RED, BLUE;

    public static final double FIELD_SIZE = 141.5;
    private static final double CENTER = FIELD_SIZE / 2;
    public boolean isRed() { return this == RED; }

    public Pose apply(Pose bluePose) {
        if (!isRed()) return bluePose;
        return new Pose(
                FIELD_SIZE - bluePose.x(),
                FIELD_SIZE - bluePose.y(),
                rotateHeading(bluePose.heading()));
    }

    public double rotateHeading(double blueHeading) {
        return isRed() ? Angle.normalizeSigned(blueHeading + Math.PI) : blueHeading;
    }

    public static Alliance fromPose(Pose pose) {
        double dx = pose.x() - CENTER;
        if (Math.abs(dx) < Constants.UNSAFE_ZONE) return null;
        return dx > 0 ? BLUE : RED;
    }
}