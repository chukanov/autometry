package ru.autometry.obd.commands.adaptor;

import ru.autometry.obd.commands.Answer;
import ru.autometry.obd.exception.OBDException;

/**
 * Created by jeck on 14/08/14
 */
public abstract class FormulaAdaptor implements Adaptor {
  private Adaptor integerAdaptor = new IntegerAdaptor();

  protected abstract Object adapt(Integer value, Answer response) throws OBDException;

  @Override
  public void adapt(Answer response) throws OBDException {
    if (response.getValue() != null && response.getValue() instanceof byte[]) {
      integerAdaptor.adapt(response);
    }
    if (response.getValue() != null && response.getValue() instanceof Integer) {
      Integer value = (Integer) response.getValue();
      response.setValue(this.adapt(value, response));
    }
  }
}
