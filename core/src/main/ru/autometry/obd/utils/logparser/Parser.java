package ru.autometry.obd.utils.logparser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by jeck on 22/08/14
 */
public class Parser {
  public static void main(String[] args) throws IOException {
    File inputFile = new File(args[0]);
    File outputFile = new File(args[1]);
    OutputStream os = null;
    try {
      os = FileUtils.openOutputStream(outputFile);
      LineIterator it = FileUtils.lineIterator(inputFile);
      while (it.hasNext()) {
        String line = it.next();
        String[] values1 = StringUtils.splitByWholeSeparator(line, "::");
        String[] values2 = StringUtils.splitByWholeSeparator(values1[1], "dump=");
        String com;
        if (StringUtils.contains(line, "rpm")) {
          com = "com1";
        } else if (StringUtils.contains(line, "altf")) {
          com = "com2";
        } else {
          com = "com3";
        }
        String ts = values1[0];
        String data = values2[1];
        IOUtils.write(ts + ";" + com + ";" + data + "\n", os, "utf-8");
      }
      os.flush();
    } finally {
      if (os != null) os.close();
    }

  }
}
