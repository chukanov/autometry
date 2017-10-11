package ru.autometry.obd.commands.adaptor.honda;

import ru.autometry.obd.commands.Answer;
import ru.autometry.obd.commands.adaptor.FormulaAdaptor;
import ru.autometry.obd.exception.OBDException;

/**
 * Created by jeck on 14/08/14
 */
public class CtLtCorrection extends FormulaAdaptor {
  @Override
  protected Object adapt(Integer value, Answer response) throws OBDException {
    double res;
    if (value == 0)
      value = 128;
    res = ((double) value / 128 - 1) * 100;
    return res;
  }
}
