package ru.autometry.obd;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by jeck on 28/07/16.
 */
public class USSDAutometry {
  public static void main(String[] args) throws Exception {

    String configFile = args[0];
    Properties props = new Properties();
    try (FileInputStream fis = new FileInputStream(new File(configFile))) {
      props.load(fis);
    }

    Worker worker = new Worker();
    worker.init(props);
    worker.work();
    while (true) {
      Thread.sleep(60000);
    }
  }
}
