package org.firstinspires.ftc.teamcode.opmodes.tests;

import static com.pedropathing.ivy.commands.Commands.instant;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.utils.Constants;
import org.firstinspires.ftc.teamcode.utils.States;

import dev.nextftc.robot.opmode.BulkReadHook;
import dev.nextftc.robot.opmode.NextOpMode;
import dev.nextftc.robot.opmode.NextTeleop;
import dev.nextftc.robot.triggers.CommandGamepad;

/**
 * PRUEBA DEL SubsystemManager + comandos (Ivy / NextFTC) SIN el chasis.
 * Úsala DESPUÉS de IntakeTest y ShooterTuner. Comprueba que la cola de estados y los comandos funcionan.
 *
 *   A = INTAKE   B = EJECT   X = STOP
 *   Y = secuencia de disparo (RPM cercana):  SPINUP -> SHOOT -> STOP
 *   RB = secuencia de disparo (RPM lejana)
 *
 * La telemetría (estado, RPM, pose) la escribe Robot.periodic().
 * Como Pedro aún no está configurado, el chasis queda inactivo (DriveSubsystem tolera follower == null).
 */
@NextTeleop(name = "Test | Secuencias (manager)", group = "Test")
public class SequenceTest extends NextOpMode {

    private final Robot robot;

    public SequenceTest(Robot robot) {
        super(robot, BulkReadHook.INSTANCE);
        this.robot = robot;
        robot.init(hardwareMap);
    }

    @Override
    public void start() {
        CommandGamepad g = new CommandGamepad(gamepad1);

        g.a().onTrue(instant(() -> robot.manager.setStates(States.INTAKE)));
        g.b().onTrue(instant(() -> robot.manager.setStates(States.EJECT)));
        g.x().onTrue(instant(() -> robot.manager.setStates(States.STOP)));
        g.y().onTrue(instant(() -> robot.manager.shootSequence(Constants.SHOOTER_RPM_CLOSE)));
        g.rightBumper().onTrue(instant(() -> robot.manager.shootSequence(Constants.SHOOTER_RPM_FAR)));
    }

    @Override
    public void periodic() { }

    @Override
    public void end() {
        robot.manager.setStates(States.STOP);
        robot.shooter.halt();
        if (robot.intake.io != null) robot.intake.io.stop();
    }
}
