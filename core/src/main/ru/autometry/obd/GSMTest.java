package ru.autometry.obd;

import ru.autometry.gsm.GSMModem;
import ru.autometry.gsm.SMSListener;

/**
 * Created by jeck on 29/07/16.
 */
public class GSMTest {
  public static void main(String[] args) throws Exception {
    GSMModem modem = new GSMModem("/dev/tty.HUAWEIMobile-Modem");
    modem.addListener(new SMSListener() {
      @Override
      public boolean receive(String message, String from, GSMModem gsm) {
        System.out.println("SMS from: "+from+", message: "+message);
        return true;
      }
    });
    modem.start(2000);
    String result = modem.sendUSSD("*312*765*s54*<~9jqo^BlbD-BleB1DJ+*+F(f,q/0JhKF<GL>Cj@.4Gp$d7F!,L7@<6@)/0JDEF<G%<+EV:2F!,O<DJ+*.@<*K0@<6L(Df-\\0Ec5e;DffZ(EZee.Bl.9pF\"~>#");
    //String result = modem.sendUSSD("*111*4440011#");
    System.out.println(result);
    modem.destroy();
  }
}
