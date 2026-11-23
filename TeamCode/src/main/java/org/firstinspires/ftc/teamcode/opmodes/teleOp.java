package org.firstinspires.ftc.teamcode.opmodes;

import static com.pedropathing.ivy.commands.Commands.instant;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.utils.Alliance;
import org.firstinspires.ftc.teamcode.utils.EndPoseSave;
import org.firstinspires.ftc.teamcode.utils.FieldPoses;
import org.firstinspires.ftc.teamcode.utils.States;
import org.firstinspires.ftc.teamcode.utils.Constants;

import dev.nextftc.robot.Telemetry;
import dev.nextftc.robot.opmode.BulkReadHook;
import dev.nextftc.robot.opmode.NextOpMode;
import dev.nextftc.robot.opmode.NextTeleop;
import dev.nextftc.robot.triggers.CommandGamepad;

@NextTeleop(name = "teleOp")
public class teleOp extends NextOpMode {

    private final Robot robot;
    private ElapsedTime timer = new ElapsedTime();

    private boolean lockPark = false;
    private boolean autoParkInit = false;
    private Alliance alliance;
    private boolean confirmed;
    private String warning = "";

    public teleOp(Robot robot){
        super(robot, BulkReadHook.INSTANCE);
        this.robot = robot;
        robot.init(hardwareMap);
        confirmed = EndPoseSave.alliance != null;
        alliance = confirmed ? EndPoseSave.alliance : Alliance.BLUE;
        applyAlliance();
    }

    @Override
    public void disabledPeriodic() {
        boolean wasConfirmed = confirmed;
        Alliance picked = alliance;
        if (gamepad1.x) { picked = Alliance.BLUE; confirmed = true; }
        if (gamepad1.b) { picked = Alliance.RED;  confirmed = true; }

        if (picked != alliance || confirmed != wasConfirmed) {
            alliance = picked;
            applyAlliance();
        }
        logAlliance();
    }

    @Override
    public void start(){
        timer.reset();

        CommandGamepad driver = new CommandGamepad(gamepad1);

        driver.a().onTrue(instant(() -> robot.manager.setStates(States.INTAKE)));
        driver.b().onTrue(instant(() -> robot.manager.setStates(States.EJECT)));
        driver.x().onTrue(instant(() -> robot.manager.setStates(States.STOP)));
        driver.y().onTrue(instant(() -> robot.manager.shootSequence(Constants.SHOOTER_RPM_CLOSE)));

        driver.rightBumper().onTrue(instant(() -> robot.manager.goShootSequence(false)));
        driver.leftBumper().onTrue(instant(() -> robot.manager.goShootSequence(true)));

        driver.dpadUp().onTrue(instant(robot.drive::resetHeading));

        driver.dpadLeft().multiPress(5,3).onTrue(instant(this::park));
        driver.dpadRight().multiPress(5, 3).onTrue(instant(() -> lockPark = true));



    }

    @Override
    public void periodic(){
        double forward = -gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        boolean sticksMoved = Math.abs(forward) + Math.abs(lateral) + Math.abs(turn) > Constants.OVERRIDE_DEADBAND;
        if (sticksMoved && robot.drive.isAutoOn()){
            robot.manager.driverOverride();   // cancela la secuencia (antes solo soltaba el chasis)
        }

        boolean slow = gamepad1.left_trigger > 0.5 || robot.manager.isSlowMode();
        robot.drive.setInput(forward,lateral,turn,slow);

        if (!autoParkInit && !lockPark && timer.seconds() > Constants.ENDGAME_PARK_S) {
            autoParkInit = true;
            park();
        }

        logAlliance();
    }



    @Override
    public void end() {
        robot.manager.setStates(States.STOP);
        robot.shooter.halt();
        if (robot.intake.io != null) robot.intake.io.stop();
        EndPoseSave.clear();   // evita usar una pose/alianza vieja en el siguiente partido (quítalo si quieres poder reiniciar a medio partido)
    }
    private void park(){
        robot.manager.setStates(States.PARK);
    }
    private void applyAlliance() {
        Pose start = alliance.apply(FieldPoses.START);
        String mismatch = "";

        Pose stored = EndPoseSave.endPose;
        Alliance saved = EndPoseSave.alliance;                                  // lo que declaró el Auto
        Alliance guess = stored == null ? null : Alliance.fromPose(stored);     // lo que dice la coordenada
        Alliance expected = saved != null ? saved : guess;                      // la declarada manda

        if (expected != null && expected != alliance) {
            mismatch = "El Auto era " + expected + " pero elegiste " + alliance;
        } else if (stored != null) {
            start = stored;                                                     // continuar donde terminó el Auto
        }

        warning = confirmed
                ? mismatch
                : "ALIANZA SIN CONFIRMAR: X = azul, B = roja" + (mismatch.isEmpty() ? "" : " | " + mismatch);

        robot.setAlliance(alliance, start);
    }

    private void logAlliance() {
        Telemetry.log("Alliance", alliance + (confirmed ? "" : " (sin confirmar)"));
        Telemetry.log("⚠", warning.isEmpty() ? "OK" : warning);
    }

}
