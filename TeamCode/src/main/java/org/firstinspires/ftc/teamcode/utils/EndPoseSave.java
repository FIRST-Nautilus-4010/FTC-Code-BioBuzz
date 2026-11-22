package org.firstinspires.ftc.teamcode.utils;

import com.pedropathing.math.Pose;

public class EndPoseSave {

    private EndPoseSave() {}

    public static Alliance alliance = null;
    public static Pose endPose = null;

    public static void save(Alliance a, Pose p) { alliance = a; endPose = p; }
    public static void clear() { alliance = null; endPose = null; }
}
