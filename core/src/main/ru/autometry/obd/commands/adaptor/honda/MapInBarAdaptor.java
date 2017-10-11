package ru.autometry.obd.commands.adaptor.honda;

import ru.autometry.obd.commands.Answer;
import ru.autometry.obd.commands.adaptor.FormulaAdaptor;
import ru.autometry.obd.exception.OBDException;
import ru.autometry.utils.common.MathUtils;

/**
 * Created by jeck on 14/08/14
 */
public class MapInBarAdaptor extends FormulaAdaptor {
  @Override
  protected Object adapt(Integer value, Answer response) throws OBDException {
    //if (MAP)
    //{
    //	temp = (MAP * 7.626) - 148.252;
    //	Data = 1013.25 - temp;
    //}
    return MathUtils.roundTo((6.91764705882353 * value) + 25.0, 0);
  }
}
