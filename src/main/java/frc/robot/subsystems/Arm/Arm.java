// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.Arm;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArmConstants;
import frc.robot.subsystems.Util.TalonFXFactory;

public class Arm extends SubsystemBase {

  private TalonFX m_armMotor = TalonFXFactory.createTalon(ArmConstants.armTalonID, ArmConstants.armTalonCANBus, ArmConstants.kArmConfiguration);
  private TalonFX m_armMotor2 = TalonFXFactory.createTalon(ArmConstants.armTalonID2, ArmConstants.armTalonCANBus, ArmConstants.kArmConfiguration);
  /** Creates a new Arm. */
  public Arm() {
  }
   /**
   * PID arm to position
   * 
   * @param rotations 0 to 1 rotations
   */
  private void setAngle(double position) {
    m_armMotor.setControl(ArmConstants.armPositionControl.withPosition(position));
    m_armMotor2.setControl(ArmConstants.armPositionControl.withPosition(position));
  }

  /**
   * PID arm to position
   * 
   * @param rotations Rotation 2d
   */
  public void setAngle(Rotation2d position) {
    setAngle(position.getRotations());
  }

  public void stow() {
    setAngle(0);
  }

  /**
   * Just PID to the current angle to hold position
   */
  public void holdPosition() {
    setAngle(getAngle());
  }

  public Rotation2d getSetpointError() {
    return Rotation2d.fromRotations(m_armMotor.getClosedLoopError().getValue());
  }

  public boolean isAtSetpoint() {
    return Math.abs(getSetpointError().getDegrees()) <= ArmConstants.angleErrorTolerance.getDegrees();
  }

  public Rotation2d getAngle() {
    return Rotation2d.fromRotations(m_armMotor.getPosition().getValue());
  }

  public double getVoltageOut() {
    return m_armMotor.getMotorVoltage().getValue();
  }

  public void stop() {
    m_armMotor.setControl(new DutyCycleOut(0));
  }

  public void setarmVoltage(double volts) {
    m_armMotor.setControl(new VoltageOut(volts));
  }

  
  // To position for Intake, move Arm to INTAKE position
    public Command prepareForIntakeCommand() {
        return new RunCommand(()-> this.setAngle(Rotation2d.fromDegrees(ArmConstants.ArmIntakeAngle)), this)
            .until(()->this.isAtSetpoint());
    }   

    public Command stowarmCommand() {
      return new RunCommand(()->this.stow(), this);
  }

  @Override
  public void periodic() {

    SmartDashboard.putNumber("armError", getSetpointError().getDegrees());
    SmartDashboard.putNumber("armCurrentAngle", getAngle().getDegrees());
  }
}
