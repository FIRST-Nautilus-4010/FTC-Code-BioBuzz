package org.firstinspires.ftc.teamcode.subsystems.intake;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.utils.Constants;

public class IntakeIo {

    private final DcMotorEx intakeMotor;

    public IntakeIo(HardwareMap hardwareMap){
        this.intakeMotor = hardwareMap.get(DcMotorEx.class, "intake");
        configureMotor();
    }

    public void setRpm(double rpm){
        intakeMotor.setVelocity(rpm * Constants.INTAKE_TICKS_PER_REV / 60);
    }
    public void stop() { intakeMotor.setPower(0); }

    public double getRpm(){
        return intakeMotor.getVelocity() / Constants.INTAKE_TICKS_PER_REV;
    }

    public double getPosition(){
        return (double) intakeMotor.getCurrentPosition() / Constants.INTAKE_TICKS_PER_REV;
    }

    public void configureMotor(){
        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }


}
