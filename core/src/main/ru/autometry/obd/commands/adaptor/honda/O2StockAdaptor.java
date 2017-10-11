package ru.autometry.obd.commands.adaptor.honda;

import ru.autometry.obd.commands.Answer;
import ru.autometry.obd.commands.adaptor.FormulaAdaptor;
import ru.autometry.obd.exception.OBDException;
import ru.autometry.utils.common.InitUtils;
import ru.autometry.utils.common.Initable;
import ru.autometry.utils.common.MathUtils;

import java.util.Properties;

/**
 * Created by jeck on 14/08/14
 */
public class O2StockAdaptor extends FormulaAdaptor implements Initable {
  public static double[] volts = {
          0.00, 0.02, 0.04, 0.06, 0.08, 0.10, 0.12, 0.14, 0.16, 0.18, 0.20, 0.22, 0.24,
          0.25, 0.27, 0.29, 0.31, 0.33, 0.35, 0.37, 0.39, 0.41, 0.43, 0.45, 0.47, 0.49,
          0.51, 0.53, 0.55, 0.57, 0.59, 0.61, 0.63, 0.65, 0.67, 0.69, 0.71, 0.73, 0.75,
          0.76, 0.78, 0.80, 0.82, 0.84, 0.86, 0.88, 0.90, 0.92, 0.94, 0.96, 0.98, 1.00,
          1.02, 1.04, 1.06, 1.08, 1.10, 1.12, 1.14, 1.16, 1.18, 1.20, 1.22, 1.24, 1.25,
          1.27, 1.29, 1.31, 1.33, 1.35, 1.37, 1.39, 1.41, 1.43, 1.45, 1.47, 1.49, 1.51,
          1.53, 1.55, 1.57, 1.59, 1.61, 1.63, 1.65, 1.67, 1.69, 1.71, 1.73, 1.75, 1.76,
          1.78, 1.80, 1.82, 1.84, 1.86, 1.88, 1.90, 1.92, 1.94, 1.96, 1.98, 2.00, 2.02,
          2.04, 2.06, 2.08, 2.10, 2.12, 2.14, 2.16, 2.18, 2.20, 2.22, 2.24, 2.25, 2.27,
          2.29, 2.31, 2.33, 2.35, 2.37, 2.39, 2.41, 2.43, 2.45, 2.47, 2.49, 2.51, 2.53,
          2.55, 2.57, 2.59, 2.61, 2.63, 2.65, 2.67, 2.69, 2.71, 2.73, 2.75, 2.76, 2.78,
          2.80, 2.82, 2.84, 2.86, 2.88, 2.90, 2.92, 2.94, 2.96, 2.98, 3.00, 3.02, 3.04,
          3.06, 3.08, 3.10, 3.12, 3.14, 3.16, 3.18, 3.20, 3.22, 3.24, 3.25, 3.27, 3.29,
          3.31, 3.33, 3.35, 3.37, 3.39, 3.41, 3.43, 3.45, 3.47, 3.49, 3.51, 3.53, 3.55,
          3.57, 3.59, 3.61, 3.63, 3.65, 3.67, 3.69, 3.71, 3.73, 3.75, 3.76, 3.78, 3.80,
          3.82, 3.84, 3.86, 3.88, 3.90, 3.92, 3.94, 3.96, 3.98, 4.00, 4.02, 4.04, 4.06,
          4.08, 4.10, 4.12, 4.14, 4.16, 4.18, 4.20, 4.22, 4.24, 4.25, 4.27, 4.29, 4.31,
          4.33, 4.35, 4.37, 4.39, 4.41, 4.43, 4.45, 4.47, 4.49, 4.51, 4.53, 4.55, 4.57,
          4.59, 4.61, 4.63, 4.65, 4.67, 4.69, 4.71, 4.73, 4.75, 4.76, 4.78, 4.80, 4.82,
          4.84, 4.86, 4.88, 4.90, 4.92, 4.94, 4.96, 4.98, 5.00};

  private O2CalcMethod calcMethod;

  @Override
  protected Object adapt(Integer value, Answer response) throws OBDException {
    switch (calcMethod) {
      case Oxygen:
        return volts[value];
      default: {
        double num2 = volts[value];
        if (num2 < 0.05) {
          num2 = 19.1;
        }
        if ((num2 >= 0.05) & (num2 <= 0.249)) {
          num2 = MathUtils.roundTo(19.1 - (4.4 * (num2 / 0.249)), 2);
        }
        if ((num2 >= 0.25) & (num2 <= 0.77)) {
          num2 = MathUtils.roundTo(14.7 - (0.1 * (num2 / 0.749)), 2);
        }
        if ((num2 > 0.77) & (num2 < 1.0)) {
          num2 = MathUtils.roundTo((10.3 * (1.0 / num2)) + 0.5, 2);
        }
        if (num2 >= 1.0) {
          return 10.3;
        }
        return num2;
      }
    }
  }

  @Override
  public void init(Properties config) throws Exception {
    calcMethod = O2CalcMethod.valueOf(InitUtils.getString("method", config));
  }

  /**
   * Created by jeck on 12/08/14
   */
  public static enum O2CalcMethod {
    Oxygen,
    ARF
  }
}
