package ru.autometry.utils.common;

import java.util.Properties;

/**
 * Created by jeck on 13/08/14
 */
public interface Initable {
  public void init(Properties config) throws Exception;
}
