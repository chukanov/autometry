package ru.autometry.obd.commands.adaptor.honda;

import ru.autometry.obd.commands.Answer;
import ru.autometry.obd.commands.adaptor.FormulaAdaptor;
import ru.autometry.obd.exception.OBDException;

/**
 * Created by jeck on 15/08/14
 */
public class BatAdaptor extends FormulaAdaptor {
  @Override
  protected Object adapt(Integer value, Answer response) throws OBDException {
    return (value * 0.05) + 6.5;
  }
}
