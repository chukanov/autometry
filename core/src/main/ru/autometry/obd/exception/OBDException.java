package ru.autometry.obd.exception;

/**
 * Created by jeck on 11/08/14
 */
public class OBDException extends Exception {
  public OBDException() {
  }

  public OBDException(String message) {

    super(message);
  }

  public OBDException(String message, Throwable cause) {
    super(message, cause);
  }

  public OBDException(Throwable cause) {
    super(cause);
  }

  public OBDException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
