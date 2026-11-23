package org.firstinspires.ftc.teamcode.utils;

import com.pedropathing.math.Pose;
import com.pedropathing.utils.Angle;

/**
 * El campo es simétrico por ROTACIÓN de 180° alrededor del centro (70.75, 70.75), no por espejo.
 * Todas las poses se definen del lado AZUL (x > 70.75) y para ROJO se rotan:
 *     x' = 141.5 - x     y' = 141.5 - y     heading' = heading + π
 */
public enum Alliance {

    RED,
    BLUE;

    public static final double FIELD_SIZE = 141.5;
    private static final double CENTER = FIELD_SIZE / 2;   // 70.75

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

    /** ¿De qué alianza parece esta pose? null si está muy cerca del centro (ambiguo). Azul = derecha. */
    public static Alliance fromPose(Pose pose) {
        double dx = pose.x() - CENTER;
        if (Math.abs(dx) < Constants.UNSAFE_ZONE) return null;
        return dx > 0 ? BLUE : RED;
    }
}
