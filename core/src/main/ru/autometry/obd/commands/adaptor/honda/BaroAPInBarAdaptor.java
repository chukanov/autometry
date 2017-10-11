package ru.autometry.obd.commands.adaptor.honda;

import ru.autometry.obd.commands.Answer;
import ru.autometry.obd.commands.adaptor.FormulaAdaptor;
import ru.autometry.obd.exception.OBDException;

/**
 * Created by jeck on 14/08/14
 */
public class BaroAPInBarAdaptor extends FormulaAdaptor {
  @Override
  protected Object adapt(Integer value, Answer response) throws OBDException {
    double x = (double) value * 5 / 256; // 5v / 255 bits = sensor range
    return ((3.6527 * x * x) + (20.349 * x) + 11.236) * 33.42;
  }
}
