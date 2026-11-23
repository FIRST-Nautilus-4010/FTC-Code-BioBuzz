package org.firstinspires.ftc.teamcode.utils;

import com.acmerobotics.dashboard.config.Config;

/**
 * Todo es "public static" y NO final => se edita EN VIVO desde FTC Dashboard (pestaña Configuration).
 * ⚠ Dashboard NO guarda los cambios: al reiniciar el robot vuelven a estos valores.
 *   Cuando algo funcione bien, copia el número aquí.
 */
@Config
public class Constants {

    // ---------------- Shooter ----------------
    public static boolean SHOOTER_REVERSED = true;
    public static double SHOOTER_TICKS_PER_REV = 28;
    /** Relación de reducción motor→flywheel (vueltas de motor por vuelta de flywheel).
     *  rpm_flywheel = rpm_motor / RATIO. Si el flywheel gira MÁS rápido que el motor, usa un número < 1. */
    public static double SHOOTER_RATIO = 1.2;
    public static double SHOOTER_FILTER_ALPHA = 0.35;    // 1 = sin filtro; menor = más suave
    public static double SHOOTER_KV = 0.0021;            // volts por RPM   (ver ShooterKvTest)
    public static double SHOOTER_KP = 0.004;             // volts por RPM de error
    public static double SHOOTER_KI = 0.0;               // déjala en 0 hasta que KV y KP estén bien
    public static double SHOOTER_TOLERANCE_RPM = 60;
    public static double SHOOTER_READY_HOLD_MS = 100;
    public static double SHOOTER_RPM_CLOSE = 3000;
    public static double SHOOTER_RPM_FAR = 3600;

    // ---------------- Intake ----------------
    public static boolean INTAKE_REVERSED = false;
    public static double INTAKE_TICKS_PER_REV = 28;      // ticks del EJE DE SALIDA (ver IntakeTest)
    public static double INTAKE_TAKE_RPM = 1500;
    public static double INTAKE_THROW_RPM = 1500;

    // ---------------- Secuencias ----------------
    public static double FEED_TIME_S = 1.2;
    public static double SPINUP_TIMEOUT_S = 3.0;
    public static double DRIVE_TIMEOUT_S = 4.0;

    // ---------------- Drive ----------------
    public static double SLOW_SCALE = 0.5;
    public static double HEADING_TOLERANCE_DEG = 4;
    public static double POSE_TOLERANCE_IN = 2;
    public static double OVERRIDE_DEADBAND = 0.1;
    public static double ENDGAME_PARK_S = 110;
    public static double DRIVER_FORWARD_BLUE_RAD = Math.toRadians(180);
    public static double UNSAFE_ZONE = 4;
}
