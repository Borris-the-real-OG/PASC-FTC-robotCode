/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="First Driver Op", group="Linear Opmode")
//@Disabled
public class FirstDriverOp extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        JesusBot robot = new JesusBot(hardwareMap);
        boolean reverseConveyor = false;
        int launcherSpeedLevel = 1;
        double maxLaunchSpeed = 200;
        boolean releasedRoller = false;

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Setup a variable for each drive wheel to save power level for telemetry
            double vertical = -gamepad1.right_stick_y;
            double horizontal = -gamepad1.right_stick_x;
            double turn = gamepad1.left_stick_x;

            double frontLeftPower = Range.clip(vertical + horizontal + turn, -1 ,1);
            double frontRightPower = Range.clip(vertical - horizontal - turn, -1, 1);
            double backLeftPower = Range.clip(vertical - horizontal + turn, -1, 1);
            double backRightPower = Range.clip(vertical + horizontal - turn, -1, 1);
            double conveyorPower = reverseConveyor ? gamepad1.left_trigger : -gamepad1.left_trigger;
            double rollerPower = gamepad1.right_trigger;

            if (gamepad1.left_bumper) reverseConveyor = !reverseConveyor;

            if (gamepad1.b){
                robot.rollerRelease.setPosition(releasedRoller?0:1);
                releasedRoller = !releasedRoller;
            }

            if (gamepad1.dpad_up) robot.armServo.setPosition(0);
            else if (gamepad1.dpad_down) robot.armServo.setPosition(1);

            if (gamepad1.dpad_left) robot.clampServo.setPosition(0);
            else if (gamepad1.dpad_right) robot.clampServo.setPosition(1);

            if (gamepad1.y)launcherSpeedLevel++;
            else if (gamepad1.a)launcherSpeedLevel+=3;
            launcherSpeedLevel%=4;
            double launcherSpeed = gamepad1.right_bumper ? maxLaunchSpeed/4*launcherSpeedLevel:0;

            // Send calculated power to wheels
            robot.frontLeft.setPower(frontLeftPower);
            robot.frontRight.setPower(frontRightPower);
            robot.backLeft.setPower(backLeftPower);
            robot.backRight.setPower(backRightPower);
            robot.conveyor.setPower(conveyorPower);
            robot.roller.setPower(rollerPower);
            robot.rightLauncher.setVelocity(launcherSpeed);

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "FL (%.2f), FR (%.2f), BL (%.2f), BR (%.2f), C (%.2f), R(%.2f)", frontLeftPower, frontRightPower, backLeftPower, backRightPower, conveyorPower, rollerPower);
            //telemetry.addData("Motors", "FL (%.2f), FR (%.2f), BL (%.2f), BR (%.2f), C (%.2f), R(%.2f)", robot.frontLeft.getPower(), robot.frontRight.getPower(), robot.backLeft.getPower(), robot.backRight.getPower(), robot.conveyor.getPower(), robot.roller.getPower());
            telemetry.addData("Encoders", "FL (%d), FR (%d), BL (%d), BR (%d)", robot.frontLeft.getCurrentPosition(), robot.frontRight.getCurrentPosition(), robot.backLeft.getCurrentPosition(), robot.backRight.getCurrentPosition());
            telemetry.addData("Servo", "Arm (%.2f), Clamp (%.2f), Release (%.2f )",robot.armServo.getPosition(),robot.clampServo.getPosition(),robot.rollerRelease.getPosition());
            telemetry.addData("Velocity",launcherSpeed);
            telemetry.update();
        }

        // Turn off all wheels when done
        robot.frontLeft.setPower(0);
        robot.frontRight.setPower(0);
        robot.backLeft.setPower(0);
        robot.backRight.setPower(0);

        // Display that robot stopped, and total run time
        telemetry.addData("Finished", "Run Time: " + runtime.toString());
        telemetry.update();
    }
}