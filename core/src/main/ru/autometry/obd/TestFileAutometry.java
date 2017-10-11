package ru.autometry.obd;

import ru.autometry.obd.config.Config;
import ru.autometry.obd.processing.CommandProcessor;
import ru.autometry.obd.processing.FileProcessor;
import ru.autometry.utils.common.InitUtils;

import java.io.File;
import java.util.Properties;

/**
 * Created by jeck on 27/07/16.
 */
public class TestFileAutometry {
  public static void main(String[] args) throws Exception {

    String configFile = args[0];
    Properties properties = InitUtils.loadProperties(configFile);

    Worker worker = new Worker() {
      @Override
      protected CommandProcessor newProcessor(Config config, Properties properties) throws Exception {
        return new FileProcessor(new File(InitUtils.getString("ecu.log.file", properties)), config);
      }
    };
    worker.init(properties);
    worker.work();
  }
}
