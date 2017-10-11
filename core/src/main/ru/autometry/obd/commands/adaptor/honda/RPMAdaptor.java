package ru.autometry.obd.commands.adaptor.honda;

import ru.autometry.obd.commands.Answer;
import ru.autometry.obd.commands.adaptor.FormulaAdaptor;
import ru.autometry.obd.exception.OBDException;
import ru.autometry.utils.common.InitUtils;
import ru.autometry.utils.common.Initable;

import java.util.Properties;

/**
 * Created by jeck on 13/08/14
 */
public class RPMAdaptor extends FormulaAdaptor implements Initable {
  private RPMCalcMethod calcMethod;
  private boolean inverseBytes;

  public static int formulaRPM(int value, RPMCalcMethod method, boolean inverseBytes) {
    int res = 0;
    if (value != 0) {
      if (inverseBytes) {
        value = ((value << 8) & 0xFF00) | ((value >> 8) & 0xFF);
      }
      switch (method) {
        case Obd1:
          res = (int) ((double) 1875000 / value);
          break;
        case Obd0:
          res = (int) ((double) 1848000 / value);
          break;
        case Obd2a:
          res = (int) (value / 4f);
          break;
        default:
          res = (int) ((double) 1920000 / value);
          break;
      }
    }
    return res;
  }


  @Override
  protected Object adapt(Integer value, Answer response) throws OBDException {
    return formulaRPM(value, calcMethod, inverseBytes);
  }

  @Override
  public void init(Properties config) throws Exception {
    calcMethod = RPMCalcMethod.valueOf(InitUtils.getString("method", config));
    inverseBytes = InitUtils.getBoolean("inverse", config);
  }

  /**
   * Created by jeck on 31/07/14
   */
  public static enum RPMCalcMethod {
    Obd0,
    Obd1,
    Obd2a,
    Other

  }
}
