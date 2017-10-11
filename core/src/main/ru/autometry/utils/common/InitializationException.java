package ru.autometry.utils.common;

/**
 * User: Jeck
 * Date: 25.06.2008
 * Time: 12:19:44
 */
public class InitializationException extends Exception {
  public InitializationException(String parameterName) {
    super("Not found parameter: \"" + parameterName + "\"");
  }
}
