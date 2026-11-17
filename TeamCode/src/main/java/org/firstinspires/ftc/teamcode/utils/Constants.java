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

    public static double SPINUP_TIMEOUT_S      = 3.0;

    public static final double INTAKE_TAKE_RPM = 0;
    public static final double INTAKE_THROW_RPM = 0;
    public static int INTAKE_TICKS_PER_REV = 28;
}
