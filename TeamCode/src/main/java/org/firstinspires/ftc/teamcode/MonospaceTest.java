package org.firstinspires.ftc.teamcode;

import android.text.Html;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name = "Monospace")
public class MonospaceTest extends LinearOpMode {
    @Override
    public void runOpMode() {
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE);

        telemetry.addLine("iiiiiiii");
        telemetry.addLine("oooo"+'\u0332'+"oooo");
        telemetry.addLine("!?@$%^&*");
        telemetry.update();
        waitForStart();
        telemetry.clearAll();
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.CLASSIC);
        telemetry.addData("test","iiiiiiii");
        telemetry.addData("haha","oooooooo");
        telemetry.update();
        sleep(10000);

    }
}
