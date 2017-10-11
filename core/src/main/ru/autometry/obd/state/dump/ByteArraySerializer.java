package ru.autometry.obd.state.dump;

import ru.autometry.obd.state.OBDState;
import ru.autometry.obd.state.location.GSMLocation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;

/**
 * Created by jeck on 08/09/16.
 */
public class ByteArraySerializer implements StateSerializer<byte[]> {
  @Override
  public byte[] serialize(OBDState state) throws Exception {
    ByteArrayOutputStream baos = null;
    DataOutputStream dos = null;
    try {
      baos = new ByteArrayOutputStream();
      dos = new DataOutputStream(baos);
      dos.writeByte(0);
      dos.writeLong(state.getRevolutions());
      dos.writeDouble(state.getDistance());
      dos.writeDouble(state.getLiters());
      dos.writeLong(state.getTime().getTime());
      dos.writeLong(state.getStopTime());
      dos.writeLong(state.getOnlineTime());
      dos.writeByte(state.getErrors().length);
      dos.writeInt(state.getSessionId());
      if (state.getLocation()!=null) {
        dos.writeShort(state.getLocation().getMcc());
        dos.writeShort(state.getLocation().getMnc());
        dos.writeInt(state.getLocation().getLac());
        dos.writeInt(state.getLocation().getCid());
      }
      return baos.toByteArray();
    } finally {
      if (dos != null) dos.close();
      if (baos != null) baos.close();
    }
  }

  @Override
  public OBDState load(byte[] bytes) throws Exception {
    ByteArrayInputStream bais = null;
    DataInputStream dis = null;
    try {
      bais = new ByteArrayInputStream(bytes);
      dis = new DataInputStream(bais);
      byte version = dis.readByte();
      if (version != 0) throw new Exception("bad version: " + version);
      OBDState state = new OBDState();
      state.setRevolutions(dis.readLong());
      state.setDistance(dis.readDouble());
      state.setLiters(dis.readDouble());
      state.setTime(new Date(dis.readLong()));
      state.setStopTime(dis.readLong());
      state.setOnlineTime(dis.readLong());
      state.setErrors(new String[dis.readByte()]);
      state.setSessionId(dis.readInt());
      GSMLocation location = new GSMLocation();
      state.setLocation(location);
      try {
        location.setMcc(dis.readShort());
        location.setMnc(dis.readShort());
        location.setLac(dis.readInt());
        location.setCid(dis.readInt());
      } catch (Exception e) {
        state.setLocation(null);
      }
      return state;
    } finally {
      if (dis != null) dis.close();
      if (bais != null) bais.close();
    }
  }
}
