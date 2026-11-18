package org.firstinspires.ftc.teamcode.opmodes;

import static com.pedropathing.ivy.commands.Commands.instant;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.States;
import org.firstinspires.ftc.teamcode.utils.Constants;

import dev.nextftc.robot.opmode.NextOpMode;
import dev.nextftc.robot.opmode.NextTeleop;
import dev.nextftc.robot.triggers.CommandGamepad;

@NextTeleop(name = "teleOp")
public class teleOp extends NextOpMode {

    private final Robot robot;

    public teleOp(Robot robot){
        super(robot);
        this.robot = robot;
        robot.init(hardwareMap);
    }

    @Override
    public void start(){
        CommandGamepad driver = new CommandGamepad(gamepad1);

        driver.a().onTrue(instant(() -> robot.manager.setStates(States.INTAKE)));
        driver.b().onTrue(instant(() -> robot.manager.setStates(States.EJECT)));
        driver.x().onTrue(instant(() -> robot.manager.setStates(States.STOP)));
        driver.y().onTrue(instant(() -> robot.manager.shootSequence(Constants.SHOOTER_RPM_CLOSE)));

        driver.rightBumper().onTrue(instant(() -> robot.manager.goShootSequence(false)));
        driver.leftBumper().onTrue(instant(() -> robot.manager.goShootSequence(true)));

        driver.dpadUp().onTrue(instant(robot.drive::resetHeading));

    }

    @Override
    public void periodic(){
        double forward = -gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        boolean sticksMoved = Math.abs(forward) + Math.abs(lateral) + Math.abs(turn) > Constants.OVERRIDE_DEADBAND;
        if (sticksMoved && robot.drive.isAutoOn()){
            robot.drive.releaseControl();
            if (robot.manager.isDrivingToPose()) robot.manager.setStates(States.TRAVEL);
        }

        boolean slow = gamepad1.left_trigger > 0.5 || robot.manager.isSlowMode();
        robot.drive.setInput(forward,lateral,turn,slow);
    }

    @Override
    public void end() {
        robot.manager.setStates(States.STOP);
        robot.shooter.halt();
        if (robot.intake.io != null) robot.intake.io.stop();
    }

}
