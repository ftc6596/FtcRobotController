package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;

@Autonomous(name="ObeliskAutoRed", group="Linear OpMode")
public class ObeliskAutoRed extends LinearOpMode {
    //Electronic Variables
    //Motors
    private DcMotorEx topMotor;
    private DcMotorEx bottomMotor;
    private DcMotor sorter = null;
    private DcMotor intake;
    private DcMotor LFront;
    private DcMotor RFront;
    private DcMotor LBack;
    private DcMotor RBack;
    //Servos
    private Servo outtakeFeeder;

    public void runOpMode() throws InterruptedException {
        //References to Electronics
        topMotor = hardwareMap.get(DcMotorEx.class, "top");
        bottomMotor = hardwareMap.get(DcMotorEx.class, "bottom");
        outtakeFeeder = hardwareMap.get(Servo.class, "feeder");
        sorter = hardwareMap.get(DcMotor.class, "sorter");
        intake = hardwareMap.get(DcMotor.class, "intake");
        LFront  = hardwareMap.get(DcMotor.class, "leftfront");
        RFront = hardwareMap.get(DcMotor.class, "rightfront");
        LBack  = hardwareMap.get(DcMotor.class, "leftback");
        RBack = hardwareMap.get(DcMotor.class, "rightback");
        //Variables
        double power = 0.34;
        boolean addedPower = false;
        boolean nextSlot = false;
        int slot = 0;
        //Setup for Electronics
        sorter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LFront.setDirection(DcMotor.Direction.REVERSE);
        LBack.setDirection(DcMotor.Direction.REVERSE);
        RFront.setDirection(DcMotor.Direction.REVERSE);
        RBack.setDirection(DcMotor.Direction.FORWARD);

        topMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        waitForStart();
        outtakeFeeder.setPosition(0.4);
        //Applies power to the shooter
        topMotor.setVelocity(800);
        bottomMotor.setVelocity(800);
        LFront.setPower(.5);
        LBack.setPower(.5);
        RFront.setPower(.5);
        RBack.setPower(.5);
        sleep(1250);
        LFront.setPower(0);
        LBack.setPower(0);
        RFront.setPower(0);
        RBack.setPower(0);
        ShootAllBalls(this, outtakeFeeder,sorter);
        LFront.setDirection(DcMotorSimple.Direction.FORWARD);
        LBack.setDirection(DcMotor.Direction.REVERSE);
        RFront.setDirection(DcMotor.Direction.REVERSE);
        RBack.setDirection(DcMotor.Direction.REVERSE);
        LFront.setPower(.5);
        LBack.setPower(.5);
        RFront.setPower(.5);
        RBack.setPower(.5);
        sleep(750);
    }
    //Changes Slot Ball State
    public static void ChangeBallSlotColor(ArrayList<String> slots, String color, int slot)
    {
        slots.set(slot, color);
    }
    //Rotates to a specified slot
    public static void RotateMotorToSlot(DcMotor sorter, int slot)
    {
        if(slot >= 2)
        {
            sorter.setTargetPosition(260);
        } else if (slot <= 0) {
            sorter.setTargetPosition(0);
        }
        else
        {
            sorter.setTargetPosition(130);
        }

        sorter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
    //Check and/or resets encoder
    public static boolean CheckNextAngle(DcMotor sorter)
    {
        if(sorter.getCurrentPosition() + 130 > 380)
        {
            sorter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            return true;
        }

        return false;
    }
    //Rotates to a specified angle
    public static void RotateMotorToAngle(DcMotor sorter, int angle)
    {
        sorter.setTargetPosition(angle);
    }
    //Rotates to the nest slot
    public static int RotateMotorToNextSlot(DcMotor sorter, int currentSlot) throws InterruptedException {
        int nextSlot = currentSlot + 1;
        if(nextSlot == 2)
        {
            sorter.setTargetPosition(260);
        }
        else if(nextSlot == 1)
        {
            sorter.setTargetPosition(130);
        }
        else if(nextSlot == 3)
        {
            sorter.setTargetPosition(380);

            return 0;
        }

        return nextSlot;
    }

    //Feeds the Shooter
    public void ShootBall(OpMode opMode, Servo outtakeFeeder) throws InterruptedException {
        outtakeFeeder.setPosition(.75);
        sleep(500);
        outtakeFeeder.setPosition(1);
        sleep(1000);
        outtakeFeeder.setPosition(.4);
        sleep(1000);
    }
    //Shoot All Balls
    public void ShootAllBalls(OpMode opMode, Servo outtakeFeeder, DcMotor sorter) throws InterruptedException {
        sleep(1000);
        ShootBall(opMode,outtakeFeeder);
        RotateMotorToSlot(sorter,1);
        if(sorter.getCurrentPosition() != sorter.getTargetPosition())//Applies power to the sorter when not at desired position
        {
            sorter.setPower(1);
        }
        else
        {
            sorter.setPower(0);
        }
        sleep(3000);
        ShootBall(opMode,outtakeFeeder);
        RotateMotorToSlot(sorter,3);
        if(sorter.getCurrentPosition() != sorter.getTargetPosition())//Applies power to the sorter when not at desired position
        {
            sorter.setPower(1);
        }
        else
        {
            sorter.setPower(0);
        }
        sleep(3000);
        ShootBall(opMode,outtakeFeeder);
    }
}