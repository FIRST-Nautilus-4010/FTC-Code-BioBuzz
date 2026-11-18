package org.firstinspires.ftc.teamcode.subsystems.shooter;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.subsystems.shooter.actions.Actions;
import org.firstinspires.ftc.teamcode.utils.Constants;

import dev.nextftc.robot.Mechanism;

public class ShooterSubsystem implements Mechanism {

    public ShooterIo io;

    private double targetRpm = 0;
    private double rpm = 0;
    private double integral = 0;
    private long lastNanos = 0;
    private long inToleranceSinceMs = -1;

    public void init(HardwareMap hardwareMap) {
        io = new ShooterIo(hardwareMap);
        halt();
    }

    @Override
    public void periodic() {
        if (io == null) return;

        rpm += Constants.SHOOTER_FILTER_ALPHA * (io.getRpm() - rpm);

        if (targetRpm <= 0) { io.stop(); return; }

        double error = targetRpm - rpm;

        long now = System.nanoTime();
        double dt = lastNanos == 0 ? 0 : (now - lastNanos) / 1e9;
        lastNanos = now;
        integral += error * dt;
        if (Constants.SHOOTER_KI > 0) {
            double max = 2.0 / Constants.SHOOTER_KI;
            integral = Math.max(-max, Math.min(max, integral));
        }

        double volts = Constants.SHOOTER_KV * targetRpm
                + Constants.SHOOTER_KP * error
                + Constants.SHOOTER_KI * integral;
        io.setVoltage(volts);

        if (Math.abs(error) <= Constants.SHOOTER_TOLERANCE_RPM) {
            if (inToleranceSinceMs < 0) inToleranceSinceMs = System.currentTimeMillis();
        } else {
            inToleranceSinceMs = -1;
        }
    }

    public Command shoot(double rpm){ return Actions.shoot(this, rpm); }
    public Command stop(){ return Actions.stop(this); }

    public void setTargetRpm(double newTarget) {
        if (newTarget != targetRpm) { integral = 0; inToleranceSinceMs = -1; }
        targetRpm = Math.max(0, newTarget);
    }

    public void halt() {
        targetRpm = 0;
        integral = 0;
        inToleranceSinceMs = -1;
        lastNanos = 0;
        if (io != null) io.stop();
    }

    public boolean isReady() {
        return targetRpm > 0 && inToleranceSinceMs >= 0
                && System.currentTimeMillis() - inToleranceSinceMs >= Constants.SHOOTER_READY_HOLD_MS;
    }

    public double getRpm()       { return rpm; }
    public double getTargetRpm() { return targetRpm; }
}
