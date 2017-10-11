package ru.autometry.utils.common;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * Created by jeck on 31/07/14
 */
public abstract class Configurator<Configurable> {
  public static final String ITEM_CLASS = "class";
  /**
   * XML tag name in config.
   */
  public static final String ITEM_PROPERTY = "property";
  /**
   * XML parameter name of adaptor in config.
   */
  public static final String PROP_PROPERTY_NAME = "name";
  /**
   * XML parameter name of adaptor in config.
   */
  public static final String PROP_PROPERTY_VALUE = "value";


  public static Properties parseProperties(HierarchicalConfiguration config) {
    Properties props = new Properties();
    List propertyNodes = config.configurationsAt(ITEM_PROPERTY);
    if (propertyNodes != null) {
      for (Object propertyNode : propertyNodes) {
        HierarchicalConfiguration sub = (HierarchicalConfiguration) propertyNode;
        String name = sub.getString("[@" + PROP_PROPERTY_NAME + "]");
        String value = sub.getString("[@" + PROP_PROPERTY_VALUE + "]");
        if (value == null) value = sub.getString(".");
        props.setProperty(name, value);
      }
    }
    return props;
  }

  /**
   * Init customization properties and data.
   *
   * @param config Config properties.
   * @return Custom config.
   * @throws Exception Exception
   */
  public abstract Configurable configure(HierarchicalConfiguration config) throws Exception;

  /**
   * Get custom logger.
   *
   * @return Logger.
   */
  protected abstract Logger getLogger();

  /**
   * Init customization properties and data from config file.
   *
   * @param file XML config file.
   * @return Custom config.
   * @throws Exception Exception.
   */
  public Configurable configure(File file) throws Exception {
    try {
      XMLConfiguration configuration = new XMLConfiguration();
      configuration.load(file);
      return configure(configuration);
    } catch (Exception e) {
      throw new Exception("Configuration error, file: " + file, e);
    }
  }

  /**
   * Init customization properties and data from config file by url.
   *
   * @param url URL is linked to config file.
   * @return Custom config.
   * @throws Exception Exception.
   */
  public Configurable configure(URL url) throws Exception {
    try {
      XMLConfiguration configuration = new XMLConfiguration(url);
      return configure(configuration);
    } catch (Exception e) {
      throw new Exception("Configuration error, file: " + url, e);
    }
  }

  /**
   * Get customization properties.
   * Add "base_servive_path" to propties if it is correct.
   *
   * @param config Config properties.
   * @return Customization properties.
   */
  protected Properties getProperties(HierarchicalConfiguration config) {
    return parseProperties(config);
  }

  /**
   * Make errod node in log about eception case.
   *
   * @param message Text massage.
   * @param th      Throwable.
   */
  protected void addError(String message, Throwable th) {
    getLogger().error(message, th);
  }
}
