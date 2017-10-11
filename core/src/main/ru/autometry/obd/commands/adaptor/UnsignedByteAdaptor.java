package ru.autometry.obd.commands.adaptor;

import ru.autometry.obd.commands.Answer;
import ru.autometry.obd.exception.OBDException;

import java.math.BigInteger;

/**
 * Created by jeck on 16/08/14
 */
public class UnsignedByteAdaptor implements Adaptor {

  @Override
  public void adapt(Answer response) throws OBDException {
    if (response.getValue() instanceof byte[]) {
      byte value = (byte) new BigInteger(response.getBytes()).intValue();
      response.setValue(value & 0xFF);
    }

  }
}
