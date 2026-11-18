package org.firstinspires.ftc.teamcode.utils;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

public class FieldPoses {
    private static final PoseFactory p = PoseFactory.degrees();

    public static final Pose START       = p.of(72, 72, 0);
    public static final Pose SHOOT_CLOSE = p.of(60, 84, 135);
    public static final Pose SHOOT_FAR   = p.of(60, 12, 112);

}
