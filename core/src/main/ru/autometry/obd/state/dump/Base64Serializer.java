package ru.autometry.obd.state.dump;

import ru.autometry.obd.state.OBDState;

/**
 * Created by jeck on 03/05/15
 */
public class Base64Serializer implements StateSerializer<String> {
  private ByteArraySerializer serializer = new ByteArraySerializer();
  @Override
  public String serialize(OBDState state) throws Exception {
    byte[] bytes = serializer.serialize(state);
    return org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
    //return Base64.getEncoder().encodeToString(bytes);
  }

  @Override
  public OBDState load(String s) throws Exception {
    byte[] bytes = org.apache.commons.codec.binary.Base64.decodeBase64(s);
    return serializer.load(bytes);
  }
}
