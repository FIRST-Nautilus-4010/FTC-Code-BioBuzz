package org.firstinspires.ftc.teamcode.subsystems.shooter;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.teamcode.utils.Constants;

public class ShooterIo {

    private final DcMotorEx shooterMotor;

    private final VoltageSensor battery;
    private long lastReadMs = 0;
    private double voltage = 12;

    public ShooterIo(HardwareMap hardwareMap){
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shooter");
        battery = hardwareMap.voltageSensor.iterator().next();
        configureMotor();
    }

    public double getTicksPerSecond(){ return shooterMotor.getVelocity(); }

    /** rpm del flywheel = ticks/s * 60 / (ticksPorVuelta * relación). Se calcula aquí para que sea editable en vivo. */
    public double getRpm(){
        return getTicksPerSecond() * 60.0 / (Constants.SHOOTER_TICKS_PER_REV * Constants.SHOOTER_RATIO);
    }

    /** Aplica "voltage" volts reales (compensado por la batería). */
    public void setVoltage(double voltage){
        shooterMotor.setPower(Math.max(0, Math.min(1, voltage / getBatteryVoltage())));
    }

    public double getBatteryVoltage(){
        long now = System.currentTimeMillis();
        if (now - lastReadMs > 250) {
            double v = battery.getVoltage();
            if (v > 1.0) voltage = v;
            lastReadMs = now;
        }
        return voltage;
    }

    public void stop(){
        shooterMotor.setPower(0);
    }

    public void configureMotor(){
        shooterMotor.setDirection(Constants.SHOOTER_REVERSED
                ? DcMotorSimple.Direction.REVERSE : DcMotorSimple.Direction.FORWARD);
        shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}
