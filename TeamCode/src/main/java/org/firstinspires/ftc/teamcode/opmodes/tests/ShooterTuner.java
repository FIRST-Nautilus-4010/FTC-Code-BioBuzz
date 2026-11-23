package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.shooter.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.utils.Constants;

/**
 * AFINADOR DEL SHOOTER usando el ShooterSubsystem REAL (el mismo código del TeleOp).
 *
 *   A = encender a TEST_RPM     B = apagar
 *   DPAD ↑/↓ = TEST_RPM ± RPM_STEP
 *   RT (mantener) = alimentar con el intake (simula un disparo) y mide la caída de RPM
 *
 * En Dashboard (Configuration -> Constants) cambia en vivo: SHOOTER_KV, SHOOTER_KP, SHOOTER_KI,
 * SHOOTER_TOLERANCE_RPM, SHOOTER_FILTER_ALPHA... y mira la gráfica de "RPM medida" vs "RPM objetivo".
 *
 * Buen ajuste =
 *   - llega a velocidad rápido y sin oscilar (sin pasarse mucho),
 *   - "Caída máx" pequeña al disparar y "Recuperación" corta,
 *   - se comporta igual con la batería llena y con la baja.
 */
@Config
@TeleOp(name = "Test | Shooter Tuner", group = "Test")
public class ShooterTuner extends LinearOpMode {

    public static double TEST_RPM = 3000;
    public static double RPM_STEP = 100;

    @Override
    public void runOpMode() {
        for (LynxModule hub : hardwareMap.getAll(LynxModule.class))
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);

        Telemetry t = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        ShooterSubsystem shooter = new ShooterSubsystem();
        IntakeSubsystem intake = new IntakeSubsystem();
        shooter.init(hardwareMap);
        intake.init(hardwareMap);

        boolean spinning = false, lastUp = false, lastDown = false;
        boolean wasFeeding = false, waitingRecovery = false;
        ElapsedTime spinTimer = new ElapsedTime();
        ElapsedTime recTimer = new ElapsedTime();
        double timeToReady = -1, dipMin = 0, recoveryS = -1;

        t.addLine("Listo. A=encender  B=apagar  DPAD=±RPM  RT=alimentar");
        t.update();
        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.a && !spinning) { spinning = true; spinTimer.reset(); timeToReady = -1; }
            if (gamepad1.b) spinning = false;

            if (gamepad1.dpad_up && !lastUp)     TEST_RPM += RPM_STEP;
            if (gamepad1.dpad_down && !lastDown) TEST_RPM = Math.max(0, TEST_RPM - RPM_STEP);
            lastUp = gamepad1.dpad_up;
            lastDown = gamepad1.dpad_down;

            shooter.setTargetRpm(spinning ? TEST_RPM : 0);
            shooter.periodic();                       // el control corre aquí, cada loop

            if (spinning && timeToReady < 0 && shooter.isReady()) timeToReady = spinTimer.seconds();

            // ---- simulación de disparo ----
            boolean feeding = spinning && gamepad1.right_trigger > 0.5;
            if (feeding && !wasFeeding) {
                dipMin = shooter.getRpm(); recoveryS = -1; recTimer.reset(); waitingRecovery = true;
            }
            if (feeding) {
                intake.io.setRpm(Constants.INTAKE_TAKE_RPM);
                dipMin = Math.min(dipMin, shooter.getRpm());
            } else if (wasFeeding) {
                intake.io.stop();
            }
            if (waitingRecovery && !feeding && shooter.isReady()) {
                recoveryS = recTimer.seconds();
                waitingRecovery = false;
            }
            wasFeeding = feeding;

            double target = shooter.getTargetRpm();
            t.addData("RPM objetivo", target);
            t.addData("RPM medida", shooter.getRpm());
            t.addData("Error RPM", target - shooter.getRpm());
            t.addData("Listo", shooter.isReady());
            t.addData("Batería V", shooter.io.getBatteryVoltage());
            t.addData("Ticks/s crudos", shooter.io.getTicksPerSecond());
            t.addData("Tiempo hasta listo (s)", timeToReady < 0 ? "-" : String.format("%.2f", timeToReady));
            t.addData("Caída máx RPM al disparar", dipMin > 0 ? String.format("%.0f", target - dipMin) : "-");
            t.addData("Recuperación tras disparo (s)", recoveryS < 0 ? "-" : String.format("%.2f", recoveryS));
            t.update();
        }

        shooter.halt();
        intake.io.stop();
    }
}
