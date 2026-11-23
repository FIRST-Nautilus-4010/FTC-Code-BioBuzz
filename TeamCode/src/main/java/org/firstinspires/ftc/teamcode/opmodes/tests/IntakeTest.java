package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.utils.Constants;

/**
 * PRUEBA DEL INTAKE (sin NextFTC ni Pedro: solo IntakeSubsystem + hardware).
 *
 *   A = recoger (TEST_RPM)    B = escupir (-TEST_RPM)
 *   X = potencia cruda (TEST_POWER)    Y = parar
 *   DPAD ↑/↓ = TEST_RPM ± RPM_STEP
 *
 * Qué comprobar:
 *   1. Dirección: A debe recoger. Si gira al revés -> Constants.INTAKE_REVERSED (y reinicia el OpMode).
 *   2. Ticks por vuelta: pon X (potencia baja), para, y mira "Ticks crudos". Gira el eje de SALIDA una
 *      vuelta a mano: la diferencia de ticks = Constants.INTAKE_TICKS_PER_REV.
 *   3. RPM: "RPM medida" debe acercarse a "RPM objetivo". Sube TEST_RPM hasta la velocidad que recoja bien.
 */
@Config
@TeleOp(name = "Test | Intake", group = "Test")
public class IntakeTest extends LinearOpMode {

    public static double TEST_RPM = 1500;
    public static double TEST_POWER = 0.5;
    public static double RPM_STEP = 100;

    private enum Mode { STOP, TAKE, OUT, POWER }

    @Override
    public void runOpMode() {
        for (LynxModule hub : hardwareMap.getAll(LynxModule.class))
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);

        Telemetry t = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        IntakeSubsystem intake = new IntakeSubsystem();
        intake.init(hardwareMap);

        Mode mode = Mode.STOP;
        boolean lastUp = false, lastDown = false;

        t.addLine("Listo. A=recoger  B=escupir  X=potencia  Y=parar  DPAD=±RPM");
        t.update();
        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.a) mode = Mode.TAKE;
            if (gamepad1.b) mode = Mode.OUT;
            if (gamepad1.x) mode = Mode.POWER;
            if (gamepad1.y) mode = Mode.STOP;

            if (gamepad1.dpad_up && !lastUp)     TEST_RPM += RPM_STEP;
            if (gamepad1.dpad_down && !lastDown) TEST_RPM = Math.max(0, TEST_RPM - RPM_STEP);
            lastUp = gamepad1.dpad_up;
            lastDown = gamepad1.dpad_down;

            double target = 0;
            switch (mode) {
                case TAKE:  target = TEST_RPM;  intake.io.setRpm(target);  break;
                case OUT:   target = -TEST_RPM; intake.io.setRpm(target);  break;
                case POWER: intake.io.setPower(TEST_POWER);                break;
                default:    intake.io.stop();
            }

            double rpm = intake.io.getRpm();
            t.addData("Modo", mode);
            t.addData("RPM objetivo", target);
            t.addData("RPM medida", rpm);
            t.addData("Error RPM", target - rpm);
            t.addData("Ticks/s", intake.io.getTicksPerSecond());
            t.addData("Ticks crudos", intake.io.getRawTicks());
            t.addData("Vueltas (según INTAKE_TICKS_PER_REV)", intake.io.getPosition());
            t.addData("INTAKE_TICKS_PER_REV", Constants.INTAKE_TICKS_PER_REV);
            t.update();
        }

        intake.io.stop();
    }
}
