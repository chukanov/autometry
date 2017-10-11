package ru.autometry.utils.common;

import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;

/**
 * Created by jeck on 30/06/14
 */
public class ByteUtils {
  private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

  public static String getHexString(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int i = 0; i < bytes.length; i++) {
      int x = bytes[i] & 0xFF;
      hexChars[i * 2] = hexArray[x >>> 4];
      hexChars[i * 2 + 1] = hexArray[x & 0x0F];
    }
    return new String(hexChars);
  }

  public static String debugHexString(byte[] bytes) {
    if (bytes == null || bytes.length == 0) return "";
    char[] hexChars = new char[bytes.length * 3 - 1];
    for (int j = 0; j < bytes.length; j++) {
      int x = bytes[j] & 0xFF;
      hexChars[j * 2 + j] = hexArray[x >>> 4];
      hexChars[j * 2 + j + 1] = hexArray[x & 0x0F];
      if (j != bytes.length - 1) hexChars[j * 2 + j + 2] = ' ';
    }
    return new String(hexChars);
  }

  public static byte[] hex2byte(String hexString) throws Exception {
    String hex = hexString.replaceAll(" ", "");
    return Hex.decodeHex(hex.toCharArray());
  }


  public static byte[] append(byte[] old, byte[] anew) {
    int oldLength = old.length;
    if (oldLength == 0) return anew;
    byte[] result = Arrays.copyOf(old, old.length + anew.length);
    System.arraycopy(anew, 0, result, oldLength, anew.length);
    return result;
  }


}
