package org.firstinspires.ftc.teamcode.subsystems.intake;

import android.service.controls.actions.CommandAction;

import com.pedropathing.ivy.Command;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.subsystems.intake.actions.IntakeActions;

import dev.nextftc.robot.Mechanism;

public class IntakeSubsystem implements Mechanism {

    public final IntakeIo io;
    public IntakeSubsystem(HardwareMap hardwareMap){
        io = new IntakeIo(hardwareMap);
    }

    public Command take() { return IntakeActions.take(this,io);}
    public Command outTake() { return IntakeActions.outTake(this,io);}
    public Command stop() { return IntakeActions.stop(this,io);}

}
