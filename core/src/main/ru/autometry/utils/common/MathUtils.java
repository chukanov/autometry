package ru.autometry.utils.common;

/**
 * Created by jeck on 31/07/14
 */
public class MathUtils {
  public static double roundTo(double d, int dz) {
    double x = Math.pow(10, dz);
    return Math.round(d * x) / x;
  }
}
