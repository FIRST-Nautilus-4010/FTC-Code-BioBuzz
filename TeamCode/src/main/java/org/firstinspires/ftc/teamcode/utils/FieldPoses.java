package org.firstinspires.ftc.teamcode.utils;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

/**
 * Poses del lado AZUL (x > 70.75). Para ROJO se convierten con alliance.apply(pose).
 * Coordenadas de Pedro: pulgadas, heading en grados (PoseFactory.degrees()).
 * ⚠ VALORES DE EJEMPLO: mídelos en tu campo.
 */
public class FieldPoses {
    private static final PoseFactory p = PoseFactory.degrees();

    public static final Pose START       = p.of(110, 20, 90);
    public static final Pose SHOOT_CLOSE = p.of(95, 95, 45);
    public static final Pose SHOOT_FAR   = p.of(85, 20, 70);
    public static final Pose PARK        = p.of(100, 30, 0);
}
