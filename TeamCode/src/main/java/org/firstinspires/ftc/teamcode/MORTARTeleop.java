package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;

@TeleOp(name="MORTARTeleop", group="Linear OpMode")
public class MORTARTeleop extends LinearOpMode {
    //Electronic Variables
    //Motors
    private DcMotor topMotor;
    private DcMotor bottomMotor;
    private DcMotor sorter = null;
    private DcMotor intake;
    private DcMotor LFront;
    private DcMotor RFront;
    private DcMotor LBack;
    private DcMotor RBack;
    //Servos
    private Servo outtakeFeeder;
    @Override
    public void runOpMode() throws InterruptedException {
        //References to Electronics
        topMotor = hardwareMap.get(DcMotor.class, "top");
        bottomMotor = hardwareMap.get(DcMotor.class, "bottom");
        outtakeFeeder = hardwareMap.get(Servo.class, "feeder");
        sorter = hardwareMap.get(DcMotor.class, "sorter");
        intake = hardwareMap.get(DcMotor.class, "intake");
        LFront  = hardwareMap.get(DcMotor.class, "leftfront");
        RFront = hardwareMap.get(DcMotor.class, "rightfront");
        LBack  = hardwareMap.get(DcMotor.class, "leftback");
        RBack = hardwareMap.get(DcMotor.class, "rightback");
        //Variables
        double power = 0.3;
        boolean addedPower = false;
        boolean nextSlot = false;
        int slot = 0;
        //Setup for Electronics
        sorter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LFront.setDirection(DcMotor.Direction.FORWARD);
        LBack.setDirection(DcMotor.Direction.FORWARD);
        RFront.setDirection(DcMotor.Direction.FORWARD);
        RBack.setDirection(DcMotor.Direction.REVERSE);
        outtakeFeeder.setPosition(0.4);
        waitForStart();
        while(opModeIsActive()) {
            //Driving
            //Inputs
            double Ly = gamepad1.left_stick_y;
            double Lx = gamepad1.left_stick_x;
            double Rx = gamepad1.right_stick_x;
            //Computing Powers
            double LeftFrontWheel = Ly + Lx - Rx;
            double RightFrontWheel = Ly - Lx - Rx;
            double LeftBackWheel = Ly - Lx + Rx;
            double RightBackWheel = Ly + Lx - Rx;

            //Feed Shooter
            if(gamepad2.right_trigger != 0)
            {
                outtakeFeeder.setPosition(1);

            }
            else
            {
                outtakeFeeder.setPosition(0.4);
            }
            //Intake
            if(gamepad1.right_trigger != 0)
            {
                intake.setPower(-1);

            }
            else
            {
                intake.setPower(0);
            }

            //Makes sure that when the robot changes from slot 2-0 it stays at the right position
            if(slot == 0 && sorter.getMode().equals(DcMotor.RunMode.RUN_TO_POSITION))
            {
                if(sorter.getCurrentPosition() == 390 && sorter.getPower() == 0.0)
                {
                    sorter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    sorter.setTargetPosition(0);
                }
                else if(sorter.getCurrentPosition() != 0 && sorter.getCurrentPosition() < 260)
                {
                    nextSlot = true;
                    RotateMotorToSlot(sorter,0);
                    sorter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                } else if (sorter.getCurrentPosition() != 0 && sorter.getCurrentPosition() != 390) {
                    nextSlot = true;
                }
            }

            //Moves the sorter to the next slot with no skipping
            if(!nextSlot)
            {
                if(gamepad2.dpad_right)
                {
                    nextSlot = true;
                    slot = RotateMotorToNextSlot(sorter, slot);
                    sorter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                }
            }
            else
            {
                if(!gamepad2.dpad_right)
                {
                    nextSlot = false;
                }
            }

            //Apply Powers
            //Sorter Power
            if(gamepad2.dpad_up)//Manual Control
            {
                sorter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                sorter.setPower(.1);
            } else if (gamepad2.dpad_down)//Manual Control
            {
                sorter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                sorter.setPower(-.1);
            } else if (gamepad2.b)//Reset Sorter Encoder
            {
                sorter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            }
            else
            {
                if(sorter.getMode().equals(DcMotor.RunMode.RUN_USING_ENCODER))//Stops supplying power to Sorter when in manual Mode
                {
                    sorter.setPower(0);
                }
                else//Always in Run to Position Mode
                {
                    if(sorter.getCurrentPosition() != sorter.getTargetPosition())//Applies power to the sorter when not at desired position
                    {
                        sorter.setPower(1);
                    }
                    else
                    {
                        sorter.setPower(0);
                    }
                }
            }
            //Applies power to the shooter
            topMotor.setPower(-power);
            bottomMotor.setPower(power);
            //Applying Power the Drive Train
            LFront.setPower(LeftFrontWheel);
            RFront.setPower(RightFrontWheel);
            LBack.setPower(LeftBackWheel);
            RBack.setPower(RightBackWheel);

            //Telemetry
            //Shooter
            telemetry.addData("Power%: ", power);
            //Sorter
            telemetry.addData("Current Slot: ", slot);
            telemetry.addData("Sorter Position: ", sorter.getCurrentPosition());
            telemetry.addData("Sorter Target Position: ", sorter.getTargetPosition());
            telemetry.addData("Sorter Mode: ", sorter.getMode());
            telemetry.update();
        }

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

    }
    //Check and/or resets encoder
    public static boolean CheckNextAngle(DcMotor sorter)
    {
        if(sorter.getCurrentPosition() + 130 > 390)
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
            sorter.setTargetPosition(390);

            return 0;
        }

        return nextSlot;
    }
}

