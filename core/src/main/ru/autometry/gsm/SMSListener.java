package ru.autometry.gsm;

/**
 * Created by jeck on 29/07/16.
 */
public interface SMSListener {
  public boolean receive(String message, String from, GSMModem modem);
}
