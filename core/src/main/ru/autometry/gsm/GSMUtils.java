package ru.autometry.gsm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by jeck on 08/05/15
 */
public class GSMUtils {
  /**
   * Get SMS message length
   *
   * @param sms message text
   * @return SMS message length
   */
  public static int getMessageLength(String sms) {
    return (sms.length() / 2 - 1);
  }

  /**
   * Reverses phone number in PDU-format
   * 1234567890a -> 1032547698Fa
   *
   * @param phone number in international number format without plus
   * @return reversed phone
   */
  public static String encodePhone(String phone) {
    String result = "";
    phone += "F";
    result += phone.charAt(1);
    result += phone.charAt(0);
    result += phone.charAt(3);
    result += phone.charAt(2);
    result += phone.charAt(5);
    result += phone.charAt(4);
    result += phone.charAt(7);
    result += phone.charAt(6);
    result += phone.charAt(9);
    result += phone.charAt(8);
    result += phone.charAt(11);
    result += phone.charAt(10);
    return result;
  }

  /**
   * Converts string to UCS2 encoding
   *
   * @param text string to encode
   * @return (message length)(message)
   * @throws IOException
   */

  public static String encodeUSC2(String text) throws UnsupportedEncodingException {
    StringBuilder result = new StringBuilder();
    byte[] msg = text.getBytes("UTF-16");
    StringBuilder content = new StringBuilder();
    for (int i = 2; i < msg.length; i++) {
      String c = Integer.toHexString((int) msg[i]);
      if (c.length() < 2) content.append("0");
      content.append(c);
    }
    String msglenPacked = Integer.toHexString(content.length() / 2);
    if (msglenPacked.length() < 2) result.append("0");
    result.append(msglenPacked);
    result.append(content);
    return result.toString().toUpperCase();
  }

}
