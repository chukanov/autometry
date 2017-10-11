package ru.autometry.utils.common;


import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;


/**
 * Created by jeck on 31/07/14
 */
public class InitUtils {

  public static String getString(String property, Properties config) throws Exception {
    String configProp = config.getProperty(property);
    if (configProp == null) throw new InitializationException(property);
    return configProp;
  }

  public static String getString(String property, String defaultValue, Properties config) {
    try {
      return getString(property, config);
    } catch (Exception e) {
      return defaultValue;
    }
  }

  public static int getInt(String property, int defaultValue, Properties config) {
    try {
      return getInt(property, config);
    } catch (Exception e) {
      return defaultValue;
    }
  }

  public static int getInt(String property, Properties config) throws Exception {
    String valueString = getString(property, config);
    return Integer.parseInt(valueString);
  }

  public static short getShort(String property, Properties config) throws Exception {
    String valueString = getString(property, config);
    return Short.parseShort(valueString);
  }

  public static short getShort(String property, short defaultValue, Properties config) throws Exception {
    try {
      return getShort(property, config);
    } catch (Exception e) {
      return defaultValue;
    }
  }

  public static float getFloat(String property, Properties config) throws Exception {
    String valueString = getString(property, config);
    return Float.parseFloat(valueString);
  }

  public static float getFloat(String property, float defaultValue, Properties config) throws Exception {
    try {
      return getFloat(property, config);
    } catch (Exception e) {
      return defaultValue;
    }
  }

  public static double getDouble(String property, Properties config) throws Exception {
    String valueString = getString(property, config);
    return Double.parseDouble(valueString);
  }

  public static double getDouble(String property, double defaultValue, Properties config) throws Exception {
    try {
      return getDouble(property, config);
    } catch (Exception e) {
      return defaultValue;
    }
  }

  public static byte getByte(String property, Properties config) throws Exception {
    String valueString = getString(property, config);
    return Byte.parseByte(valueString);
  }

  public static byte getByte(String property, byte defaultValue, Properties config) throws Exception {
    try {
      return getByte(property, config);
    } catch (Exception e) {
      return defaultValue;
    }
  }

  public static boolean getBoolean(String property, Properties config) throws Exception {
    String valueString = getString(property, config);
    return Boolean.parseBoolean(valueString);
  }

  public static boolean getBoolean(String property, boolean defaultValue, Properties config) {
    try {
      return getBoolean(property, config);
    } catch (Exception e) {
      return defaultValue;
    }
  }

  public static Pattern getPattern(String property, Properties config) throws Exception {
    String valueString = getString(property, config);
    return Pattern.compile(valueString, Pattern.DOTALL | Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
  }

  public static Pattern getPattern(String property, String defaultPattern, Properties config) {
    try {
      return getPattern(property, config);
    } catch (Exception e) {
      if (defaultPattern != null) {
        return Pattern.compile(defaultPattern, Pattern.DOTALL | Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
      } else {
        return null;
      }
    }
  }

  public static long getLong(String property, Properties config) throws Exception {
    String valueString = getString(property, config);
    return Long.parseLong(valueString);
  }

  public static long getLong(String property, long defaultValue, Properties config) {
    try {
      return getLong(property, config);
    } catch (Exception e) {
      return defaultValue;
    }
  }

  public static Properties getPropertiesWoPrefix(String prefix, Properties config) throws Exception {
    Properties configWithPrefix = getPropertiesWithPrefix(prefix, config);
    Properties result = new Properties();
    for (Object keyObj : configWithPrefix.keySet()) {
      String key = (String) keyObj;
      String resultKey = key.substring(prefix.length());
      result.setProperty(resultKey, configWithPrefix.getProperty(key));
    }
    return result;
  }

  private static String getPropertyNamePrefix(String propertyName) {
    int dotPosition = propertyName.indexOf(".");
    if (dotPosition < 0) dotPosition = propertyName.length();
    return propertyName.substring(0, dotPosition);
  }

  public static Map<String, Properties> getPropertiesMap(Properties config) throws Exception {
    Map<String, Properties> rezult = new TreeMap<String, Properties>();
    for (Object propertyNameObj : config.keySet()) {
      String propertyName = (String) propertyNameObj;
      String prefix = getPropertyNamePrefix(propertyName);
      if (!rezult.containsKey(prefix)) {
        Properties prefixProperties = getPropertiesWoPrefix(prefix + ".", config);
        rezult.put(prefix, prefixProperties);
      }
    }
    return rezult;
  }

  public static Properties getPropertiesWithPrefix(String prefix, Properties config) throws Exception {
    Properties result = new Properties();
    for (Object keyObj : config.keySet()) {
      String key = (String) keyObj;
      if (key.startsWith(prefix)) {
        result.setProperty(key, config.getProperty(key));
      }
    }
    if (result.size() == 0) throw new InitializationException("Can't find properties with prefix: " + prefix);
    return result;
  }

  @SuppressWarnings("unchecked")
  public static List<String> getOrderedStringsStartsWith(String prefix, Properties config) throws Exception {
    Properties prefixConfig = getPropertiesWithPrefix(prefix, config);
    Set keySet = prefixConfig.keySet();
    Set<String> orderedKeySet = new TreeSet<String>();
    orderedKeySet.addAll(keySet);
    List<String> result = new ArrayList<String>(orderedKeySet.size());
    for (String key : orderedKeySet) {
      result.add(prefixConfig.getProperty(key));
    }
    return result;
  }

  public static Map<String, String> getMapStringsStartsWith(String prefix, Properties config) {
    Map<String, String> result = new HashMap<String, String>();
    for (Object keyObj : config.keySet()) {
      String key = (String) keyObj;
      if (key.startsWith(prefix)) {
        String propKey = key.substring(prefix.length(), key.length());
        result.put(propKey, config.getProperty(key));
      }
    }
    return result;
  }


  public static Properties loadProperties(String filename) throws IOException {
    Properties config = new Properties();
    config.load(InitUtils.class.getClassLoader().getResourceAsStream(filename));
    return config;
  }

}
