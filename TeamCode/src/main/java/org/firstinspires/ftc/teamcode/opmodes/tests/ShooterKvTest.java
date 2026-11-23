package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.shooter.ShooterIo;

import java.util.ArrayList;
import java.util.List;

/**
 * BARRIDO DE VOLTAJE PARA ENCONTRAR kV  (lazo ABIERTO: sin PID).
 *
 *   A = iniciar barrido      B = abortar
 *
 * Aplica V_START, V_START+V_STEP, ... hasta V_END volts. En cada paso espera SETTLE_S para que el
 * flywheel se estabilice y promedia las RPM durante MEASURE_S.
 *
 * Resultado:  kV = volts / rpm.  Usa el valor "V/rpm" del paso más cercano a tu RPM de trabajo,
 * o la pendiente del ajuste. Cópialo a Constants.SHOOTER_KV.
 *
 * ⚠ El flywheel girará con fuerza: zona despejada. Si SHOOTER_TICKS_PER_REV / SHOOTER_RATIO están mal,
 *   las RPM saldrán mal: verifica con IntakeTest/ShooterTuner antes de fiarte de kV.
 */
@Config
@TeleOp(name = "Test | Shooter kV Sweep", group = "Test")
public class ShooterKvTest extends LinearOpMode {

    public static double V_START = 4;
    public static double V_END = 10;
    public static double V_STEP = 2;
    public static double SETTLE_S = 3.0;
    public static double MEASURE_S = 1.0;

    @Override
    public void runOpMode() {
        for (LynxModule hub : hardwareMap.getAll(LynxModule.class))
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);

        Telemetry t = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        ShooterIo io = new ShooterIo(hardwareMap);

        List<double[]> samples = new ArrayList<>();   // {volts, rpm}
        boolean running = false;
        int step = 0;
        ElapsedTime stepTimer = new ElapsedTime();
        double rpmSum = 0;
        int rpmN = 0;

        t.addLine("Listo. A = iniciar barrido, B = abortar");
        t.update();
        waitForStart();

        while (opModeIsActive()) {
            double rpm = io.getRpm();

            if (gamepad1.a && !running) {
                running = true; step = 0; samples.clear();
                stepTimer.reset(); rpmSum = 0; rpmN = 0;
            }
            if (gamepad1.b) { running = false; }

            String status = "Detenido";
            if (running) {
                double v = V_START + step * V_STEP;
                if (v > V_END + 1e-6) {
                    running = false;
                    io.stop();
                } else {
                    io.setVoltage(v);
                    double tt = stepTimer.seconds();
                    status = String.format("Paso %d: %.1f V  (%s)", step + 1, v,
                            tt < SETTLE_S ? "estabilizando" : "midiendo");
                    if (tt >= SETTLE_S) { rpmSum += rpm; rpmN++; }
                    if (tt >= SETTLE_S + MEASURE_S) {
                        samples.add(new double[]{v, rpmN > 0 ? rpmSum / rpmN : 0});
                        step++;
                        stepTimer.reset(); rpmSum = 0; rpmN = 0;
                    }
                }
            } else {
                io.stop();
            }

            t.addData("Estado", status);
            t.addData("RPM actual", rpm);
            t.addData("Batería V", io.getBatteryVoltage());

            // ---- resultados ----
            for (int i = 0; i < samples.size(); i++) {
                double v = samples.get(i)[0], r = samples.get(i)[1];
                t.addData("Paso " + (i + 1), String.format("%.1f V -> %.0f rpm   V/rpm = %.5f", v, r, r > 0 ? v / r : 0));
            }
            if (samples.size() >= 2) {
                double mv = 0, mr = 0, num = 0, den = 0, vr = 0, rr = 0;
                for (double[] s : samples) { mv += s[0]; mr += s[1]; vr += s[0] * s[1]; rr += s[1] * s[1]; }
                mv /= samples.size(); mr /= samples.size();
                for (double[] s : samples) { num += (s[1] - mr) * (s[0] - mv); den += (s[1] - mr) * (s[1] - mr); }
                if (den > 0) {
                    double slope = num / den;
                    t.addData("kV (pendiente del ajuste)", String.format("%.5f", slope));
                    t.addData("Offset (volts a 0 rpm, ~kS)", String.format("%.2f", mv - slope * mr));
                }
                if (rr > 0) t.addData("kV (por el origen)", String.format("%.5f", vr / rr));
            }
            t.update();
        }

        io.stop();
    }
}
