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

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;


class JesusBot {
    final static int ONE_METER = 4500; // guess and check lmao
    final static int ONE_CENTIMETER =  ONE_METER / 100;
    final static int FULL_ROBOTATION = 2750;
    final static float ONE_DEGREE = FULL_ROBOTATION / 360;

    // substitute value right here                look below
    static double MAX_VELOCITY = (ONE_CENTIMETER *    12      ) / (2 * Math.sqrt(2)); // UPDATE OFTEN: distance dc motors, operating at full speed, travel in 1 sec

    // OpMode members used by MecanumWheels
    DcMotor leftLauncher;
    DcMotor rightLauncher;
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;
    DcMotor roller;
    DcMotor conveyor;
    Servo armServo;
    Servo clampServo;
    //Servo rollerRelease;

    JesusBot(HardwareMap hardwareMap){

        // Define all hardware
        armServo = hardwareMap.get(Servo.class, "armServo");
        clampServo = hardwareMap.get(Servo.class, "clampServo");
        //rollerRelease = hardwareMap.get(Servo.class, "rollerRelease");
        roller = hardwareMap.get(DcMotor.class, "roller");
        conveyor = hardwareMap.get(DcMotor.class, "conveyor");
        leftLauncher = hardwareMap.get(DcMotor.class, "leftLauncher");
        rightLauncher = hardwareMap.get(DcMotor.class, "rightLauncher");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class,"backLeft");
        backRight = hardwareMap.get(DcMotor.class,"backRight");

        //Set motor direction
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.FORWARD);
        conveyor.setDirection(DcMotor.Direction.FORWARD);
        roller.setDirection(DcMotor.Direction.FORWARD);
        leftLauncher.setDirection(DcMotor.Direction.FORWARD);
        rightLauncher.setDirection(DcMotor.Direction.REVERSE);

        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        conveyor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        roller.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftLauncher.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightLauncher.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void strafe(int cm, double rd, double pow) {
        rd %= (2 * Math.PI);
        double x = Math.cos(rd); // trig functions take radians
        double y = Math.sin(rd);

        double frontLeftPower = Range.clip(y + x, -1 ,1); // don't think we need turn if we just need to strafe
        double frontRightPower = Range.clip(y - x, -1, 1);
        double backLeftPower = Range.clip(y - x, -1, 1);
        double backRightPower = Range.clip(y + x, -1, 1);

        if (Math.abs(frontLeftPower) > 1 || Math.abs(backLeftPower) > 1 || Math.abs(frontRightPower) > 1 || Math.abs(backRightPower) > 1 ) {
            // Find the largest power
            double max = 0;
            max = Math.max(Math.abs(frontLeftPower), Math.abs(backLeftPower));
            max = Math.max(Math.abs(frontRightPower), max);
            max = Math.max(Math.abs(backRightPower), max);

            // Divide everything by max (it's positive so we don't need to worry
            // about signs)
            frontLeftPower *= pow/max; // If you don't wanna go vrooom vroom
            backLeftPower *= pow/max;
            frontRightPower *= pow/max;
            backRightPower *= pow/max;
        }

        rd = (rd <= Math.PI) ? rd : rd - Math.PI; // this is because I'm bad at math and equation below works for 0 <= x <= pi

        double actual_v = (2 * Math.sqrt(2)) / (Math.abs(Math.cos(rd)) + Math.sin(rd)); // See my bs math

        long time = (long)(cm / (MAX_VELOCITY * actual_v));

        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);

       try{
           Thread.sleep(time);
       } catch (Exception e){
           e.printStackTrace();
       }

        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        // fast_v / actual_v = constant to multiply time by
        /*
        Forward velocity is 2sqrt(2) * unit_v, basically the y-component of each mech. wheel
        strafing at 45 deg to x-axis is 2*unit_v
        strafing at 0 deg is 2sqrt(2) * unit_v
        strafing at 30 deg to x-axis is 2.689 * unit_v

        ostensibly, the wheels are at a 45 deg angle so that horizontal and vertical movement are the same

         */
    }

    public void strafe(int cm, float dg, double pow) { // no default arguments... trash lang
        double rd = dg * 2 * Math.PI / 360;
        strafe(cm, rd, pow);
    }

    public void strafe(int cm, float dg) {
        double rd = dg * 2 * Math.PI / 360;
        strafe(cm, rd, 1);
    }

    public void strafe(int cm, double rd) {
        strafe(cm, rd, 1);
    }

    //public turn(int dg) {
    //
    //}
}