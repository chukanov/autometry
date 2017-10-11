package ru.autometry.obd.state;

import ru.autometry.obd.state.location.GSMLocation;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by jeck on 14/09/14
 */
public class OBDState implements Cloneable{
  private long revolutions;
  private double distance;
  private double liters;
  private Date time = new Date();
  private long stopTime;
  private long onlineTime;
  private int sessionId;
  private String[] errors = {};

  private GSMLocation location = new GSMLocation();

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public double getLiters() {
    return liters;
  }

  public void setLiters(double liters) {
    this.liters = liters;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public String[] getErrors() {
    return errors;
  }

  public void setErrors(String[] errors) {
    this.errors = errors;
  }

  public long getStopTime() {
    return stopTime;
  }

  public void setStopTime(long stopTime) {
    this.stopTime = stopTime;
  }

  public long getRevolutions() {
    return revolutions;
  }

  public void setRevolutions(long revolutions) {
    this.revolutions = revolutions;
  }

  public long getOnlineTime() {
    return onlineTime;
  }

  public void setOnlineTime(long onlineTime) {
    this.onlineTime = onlineTime;
  }

  public int getSessionId() {
    return sessionId;
  }

  public void setSessionId(int sessionId) {
    this.sessionId = sessionId;
  }

  public GSMLocation getLocation() {
    return location;
  }

  public void setLocation(GSMLocation location) {
    this.location = location;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    OBDState state = (OBDState) o;

    if (revolutions != state.revolutions) return false;
    if (Double.compare(state.distance, distance) != 0) return false;
    if (Double.compare(state.liters, liters) != 0) return false;
    if (stopTime != state.stopTime) return false;
    if (onlineTime != state.onlineTime) return false;
    if (sessionId != state.sessionId) return false;
    if (!time.equals(state.time)) return false;
    // Probably incorrect - comparing Object[] arrays with Arrays.equals
    if (!Arrays.equals(errors, state.errors)) return false;
    return location.equals(state.location);

  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = (int) (revolutions ^ (revolutions >>> 32));
    temp = Double.doubleToLongBits(distance);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(liters);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + time.hashCode();
    result = 31 * result + (int) (stopTime ^ (stopTime >>> 32));
    result = 31 * result + (int) (onlineTime ^ (onlineTime >>> 32));
    result = 31 * result + sessionId;
    result = 31 * result + Arrays.hashCode(errors);
    result = 31 * result + location.hashCode();
    return result;
  }

  public OBDState clone() {
    try {
      OBDState state = (OBDState) super.clone();
      state.setLocation(location.clone());
    } catch (CloneNotSupportedException e) {
      //do nothing
    }
    return null;
  }

  @Override
  public String toString() {
    return "OBDState{" +
            "r=" + revolutions +
            ", d=" + distance +
            ", l=" + liters +
            ", t=" + time +
            ", sT=" + stopTime +
            ", oT=" + onlineTime +
            ", sId=" + sessionId +
            ", e=" + Arrays.toString(errors) +
            ", l=" + location +
            '}';
  }
}
