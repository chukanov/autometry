package ru.autometry.obd.commands.adaptor.honda;

import ru.autometry.obd.commands.Answer;
import ru.autometry.obd.commands.adaptor.FormulaAdaptor;
import ru.autometry.obd.exception.OBDException;

/**
 * Created by jeck on 13/08/14
 */
public class IngAdv1FAngleAdaptor extends FormulaAdaptor {

  @Override
  protected Object adapt(Integer value, Answer response) throws OBDException {
    // Угол опережения
    return (value - 24L) / 4d;
    //return (double)value / 32832;
  }
}
