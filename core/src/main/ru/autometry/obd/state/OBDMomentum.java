package ru.autometry.obd.state;

/**
 * Created by jeck on 18/09/14
 */
public class OBDMomentum {
  private OBDState currentState;
  private OBDState previousState;

  public OBDState getCurrentState() {
    return currentState;
  }

  public void setCurrentState(OBDState currentState) {
    this.currentState = currentState;
  }

  public OBDState getPreviousState() {
    return previousState;
  }

  public void setPreviousState(OBDState previousState) {
    this.previousState = previousState;
  }

  private int rpm;
  private double vss;
  private double battaryVoltage;

  public int getRpm() {
    return rpm;
  }

  public void setRpm(int rpm) {
    this.rpm = rpm;
  }

  public double getVss() {
    return vss;
  }

  public void setVss(double vss) {
    this.vss = vss;
  }


  public double getBattaryVoltage() {
    return battaryVoltage;
  }

  public void setBattaryVoltage(double battaryVoltage) {
    this.battaryVoltage = battaryVoltage;
  }
}
