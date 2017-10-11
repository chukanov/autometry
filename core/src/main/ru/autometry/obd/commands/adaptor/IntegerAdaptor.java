package ru.autometry.obd.commands.adaptor;

import ru.autometry.obd.commands.Answer;

import java.math.BigInteger;

/**
 * Created by jeck on 13/08/14
 */
public class IntegerAdaptor implements Adaptor {

  @Override
  public void adapt(Answer response) {
    if (response.getValue() instanceof byte[]) {
      BigInteger value = new BigInteger(response.getBytes());
      response.setValue(value.intValue());
    }
  }
}
