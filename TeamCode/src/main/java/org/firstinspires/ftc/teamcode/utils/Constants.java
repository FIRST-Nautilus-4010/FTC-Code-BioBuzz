package org.firstinspires.ftc.teamcode.utils;

public class Constants {

    public static final double SHOOTER_TICKS_PER_REV = 28;
    public static final double SHOOTER_RATIO = 1.2;
    public static final double SHOOTER_RPM = SHOOTER_RATIO * SHOOTER_TICKS_PER_REV;
    public static final double SHOOTER_FILTER_ALPHA = 0.35;
    public static final int SHOOTER_KI = 0;
    public static final double SHOOTER_KV = 0.0021;
    public static final double SHOOTER_KP = 0.004;
    public static final double SHOOTER_TOLERANCE_RPM = 60;
    public static final double SHOOTER_READY_HOLD_MS = 100;
    public static final double SLOW_SCALE = 0.5;
    public static final double HEADING_TOLERANCE_DEG = 1;
    public static final double POSE_TOLERANCE_IN = 1;
    public static final double SHOOTER_RPM_CLOSE = 0;
    public static final double SHOOTER_RPM_FAR = 0;
    public static final double FEED_TIME_S = 0;
    public static final double OVERRIDE_DEADBAND = 0;

    public static double SPINUP_TIMEOUT_S      = 3.0;

    public static final double INTAKE_TAKE_RPM = 0;
    public static final double INTAKE_THROW_RPM = 0;
    public static int INTAKE_TICKS_PER_REV = 28;
}
