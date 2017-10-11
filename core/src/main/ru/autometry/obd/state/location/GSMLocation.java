package ru.autometry.obd.state.location;

import ru.autometry.gsm.GSMModem;

/**
 * Created by jeck on 08/05/15
 */
public class GSMLocation implements Cloneable {
  private short mcc;
  private short mnc;
  private int lac;
  private int cid;

  public int getMcc() {
    return mcc;
  }

  public void setMcc(short mcc) {
    this.mcc = mcc;
  }

  public int getMnc() {
    return mnc;
  }

  public void setMnc(short mnc) {
    this.mnc = mnc;
  }

  public int getLac() {
    return lac;
  }

  public void setLac(int lac) {
    this.lac = lac;
  }

  public int getCid() {
    return cid;
  }

  public void setCid(int cid) {
    this.cid = cid;
  }

  public GSMLocation clone() {
    try {
      return (GSMLocation) super.clone();
    } catch (CloneNotSupportedException e) {
      //do nothing
    }
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GSMLocation location = (GSMLocation) o;

    if (mcc != location.mcc) return false;
    if (mnc != location.mnc) return false;
    if (lac != location.lac) return false;
    return cid == location.cid;

  }

  @Override
  public int hashCode() {
    int result = (int) mcc;
    result = 31 * result + (int) mnc;
    result = 31 * result + lac;
    result = 31 * result + cid;
    return result;
  }

  @Override
  public String toString() {
    return "GSMLocation{" +
            "mcc=" + mcc +
            ", mnc=" + mnc +
            ", lac=" + lac +
            ", cid=" + cid +
            '}';
  }
}
