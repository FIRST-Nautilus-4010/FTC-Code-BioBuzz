package org.firstinspires.ftc.teamcode.subsystems.intake.actions;


import static com.pedropathing.ivy.commands.Commands.instant;

import com.pedropathing.ivy.Command;

import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeIo;
import org.firstinspires.ftc.teamcode.utils.Constants;

import dev.nextftc.robot.Mechanism;

public class IntakeActions {

    private IntakeActions(){}

    public static Command take(Mechanism owner, IntakeIo io) {
        return owner.instant(() -> io.setRpm(Constants.INTAKE_TAKE_RPM));
    }

    public static Command outTake(Mechanism owner, IntakeIo io){
        return owner.instant(() -> io.setRpm(-Constants.INTAKE_THROW_RPM));
    }

    public static Command stop(Mechanism owner, IntakeIo io){
        return owner.instant(io::stop);
    }




}
