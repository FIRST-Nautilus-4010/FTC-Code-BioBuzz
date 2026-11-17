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
        this.shooterMotor = hardwareMap.get(DcMotorEx.class, "shooter");
        battery = hardwareMap.voltageSensor.iterator().next();
        configureMotor();
    }
    public double getRpm(){
        return shooterMotor.getVelocity() * (60 / Constants.SHOOTER_RPM);
    }

    public void setVoltage(double voltage){
        shooterMotor.setPower(Math.max(0,Math.min(1,voltage / getBatteryVoltage())));
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
        shooterMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }


}