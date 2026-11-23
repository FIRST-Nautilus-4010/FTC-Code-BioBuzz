package org.firstinspires.ftc.teamcode;

import com.pedropathing.ivy.Command;

import org.firstinspires.ftc.teamcode.subsystems.drive.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.shooter.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.utils.Alliance;
import org.firstinspires.ftc.teamcode.utils.Constants;
import org.firstinspires.ftc.teamcode.utils.FieldPoses;
import org.firstinspires.ftc.teamcode.utils.States;

import java.util.LinkedList;
import java.util.Queue;

public class SubsystemManager {

    private final DriveSubsystem drive;
    private final IntakeSubsystem intake;
    private final ShooterSubsystem shooter;

    private final Queue<States> statesQueue = new LinkedList<>();
    private States cachedState = null;
    private long stateStartNanos = 0;
    private double shotRpm = Constants.SHOOTER_RPM_CLOSE;
    private boolean drivenSequence = false;

    private Alliance alliance = Alliance.BLUE;

    public SubsystemManager(DriveSubsystem drive, IntakeSubsystem intake, ShooterSubsystem shooter) {
        this.drive = drive;
        this.intake = intake;
        this.shooter = shooter;
    }

    public void reset(Alliance alliance) {
        this.alliance = alliance;
        statesQueue.clear();
        cachedState = null;
    }

    public void scheduleState(States state){
        if (state == null) return;
        statesQueue.add(state);
    }

    public void setStates(States state){
        if (state == null) return;
        statesQueue.clear();
        statesQueue.add(state);
        cachedState = null;
        drivenSequence = false;
    }

    public void shootSequence(double rpm){
        shotRpm = rpm;
        setStates(States.SPINUP);
        scheduleState(States.SHOOT);
        scheduleState(States.STOP);
    }

    public void goShootSequence(boolean far){
        shotRpm = far ? Constants.SHOOTER_RPM_FAR : Constants.SHOOTER_RPM_CLOSE;
        setStates(far ? States.GO_SHOOT_FAR : States.GO_SHOOT_CLOSE);
        drivenSequence = true;                 // después de setStates, que lo pone en false
        scheduleState(States.SPINUP);
        scheduleState(States.SHOOT);
        scheduleState(States.STOP);
    }
    public void park(){
        setStates(States.PARK);
    }

    public void driverOverride(){
        drive.releaseControl();
        if (drivenSequence || isDrivingToPose()){   // isDrivingToPose cubre también PARK
            shooter.halt();
            intake.stop().schedule();
            setStates(States.TRAVEL);
        }
    }

    public void periodic() {
        if (statesQueue.isEmpty()) {
            scheduleState(States.TRAVEL);
        }

        States currentState = statesQueue.peek();

        if (cachedState != currentState){
            cachedState = currentState;
            stateStartNanos = System.nanoTime();
            Command command = buildCommand(currentState);
            if (command != null) command.schedule();
        }
        if (isStateDone(currentState) && statesQueue.size() > 1) {
            statesQueue.poll();
        }

    }

    private Command buildCommand(States state){
        switch (state){
            case INTAKE:
                return intake.take();
            case EJECT:
                return intake.outTake();
            case GO_SHOOT_CLOSE:
                shooter.setTargetRpm(shotRpm);
                drive.setTarget(alliance.apply(FieldPoses.SHOOT_CLOSE));
                return null;
            case GO_SHOOT_FAR:
                shooter.setTargetRpm(shotRpm);
                drive.setTarget(alliance.apply(FieldPoses.SHOOT_FAR));
                return null;
            case PARK:
                drive.setTarget(alliance.apply(FieldPoses.PARK));
                return null;
            case SPINUP:
                return shooter.shoot(shotRpm);
            case SHOOT:
                return intake.take();
            case STOP:
                shooter.halt();
                return intake.stop();
            case TRAVEL:
            default:
                return null;
        }
    }

    private boolean isStateDone(States state) {
        switch (state){
            case GO_SHOOT_CLOSE:
            case GO_SHOOT_FAR:
            case PARK:
                return drive.atTarget() || stateSeconds() > Constants.DRIVE_TIMEOUT_S;
            case SPINUP:
                return shooter.isReady() || stateSeconds() > Constants.SPINUP_TIMEOUT_S;
            case SHOOT:
                return stateSeconds() >= Constants.FEED_TIME_S;
            case INTAKE:
            case EJECT:
            case STOP:
                return true;
            case TRAVEL:
            default:
                return false;
        }
    }

    private double stateSeconds() { return (System.nanoTime() - stateStartNanos) / 1e9; }

    public boolean isSlowMode()      { return statesQueue.peek() == States.INTAKE; }

    public boolean isDrivingToPose() {
        States s = statesQueue.peek();
        return s == States.GO_SHOOT_CLOSE || s == States.GO_SHOOT_FAR || s == States.PARK;
    }

    public String getCurrentState() {
        States current = statesQueue.peek();
        return current == null ? "UNKNOWN" : current.toString();
    }
}
