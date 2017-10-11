package ru.autometry.obd.commands.adaptor.honda;

import ru.autometry.obd.commands.Answer;
import ru.autometry.obd.commands.adaptor.FormulaAdaptor;
import ru.autometry.obd.exception.OBDException;
import ru.autometry.utils.common.InitUtils;
import ru.autometry.utils.common.Initable;

import java.util.Properties;

/**
 * Created by jeck on 14/08/14
 */
public class IngTimeAdaptor extends FormulaAdaptor implements Initable {
  boolean inverseHiLoOrder;

  @Override
  protected Object adapt(Integer value, Answer response) throws OBDException {
    // Время форсунки
    // word value = ((int)hi<<8) + lo
    //InjTime = 3.2 *(Word)value // 1000 У доктроника
    //Injtime=(time_h*100)+(time_l*100/255);// У Starick, не пойму я ее
    //Injtime= (value / 400d) + 0.6d; // Где-то в исходниках видел
    if (inverseHiLoOrder) {
      value = ((value << 8) & 0xFF00) | ((value >> 8) & 0xFF);
    }
    //double res = 3.2d * value / 1000;
    return (((value >> 8) * 100d) + ((value & 0xff) * 100 / 255d)) / 100;
  }

  @Override
  public void init(Properties config) throws Exception {
    inverseHiLoOrder = InitUtils.getBoolean("inverse", false, config);
  }
}
