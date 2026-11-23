package org.firstinspires.ftc.teamcode.subsystems.intake;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.utils.Constants;

public class IntakeIo {

    private final DcMotorEx intakeMotor;

    public IntakeIo(HardwareMap hardwareMap){
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intake");
        configureMotor();
    }

    /** setVelocity() recibe ticks/segundo => rpm * ticksPorVuelta / 60 */
    public void setRpm(double rpm){
        intakeMotor.setVelocity(rpm * Constants.INTAKE_TICKS_PER_REV / 60.0);
    }
    public void setPower(double power){ intakeMotor.setPower(power); }   // potencia cruda (para pruebas)
    public void stop() { intakeMotor.setPower(0); }

    public double getTicksPerSecond(){ return intakeMotor.getVelocity(); }
    public double getRpm(){ return getTicksPerSecond() / Constants.INTAKE_TICKS_PER_REV * 60.0; }
    public int getRawTicks(){ return intakeMotor.getCurrentPosition(); }
    public double getPosition(){ return (double) getRawTicks() / Constants.INTAKE_TICKS_PER_REV; }

    public void configureMotor(){
        intakeMotor.setDirection(Constants.INTAKE_REVERSED
                ? DcMotorSimple.Direction.REVERSE : DcMotorSimple.Direction.FORWARD);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
