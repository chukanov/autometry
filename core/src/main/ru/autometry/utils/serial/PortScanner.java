package ru.autometry.utils.serial;

import jssc.SerialPortList;
import org.apache.log4j.Logger;

/**
 * Created by jeck on 24/08/16.
 */
public class PortScanner {
  private static Logger log  = Logger.getLogger(PortScanner.class);

  public String lookup(byte[] testCommand, Tester tester) {
    for (String device: SerialPortList.getPortNames()) {
      if (device.startsWith("/dev/tty")) {
        if (log.isInfoEnabled()) {
          log.info("trying device: "+device);
        }
        try (SyncPortReader reader = new SyncPortReader(device, 1000, 0)) {
          byte[] answerBytes = reader.execute(testCommand);
          if (tester.isCorrect(answerBytes)) return device;
        } catch (Exception e) {
          log.info("device "+device+" exception: ", e);
        }
      }
    }
    return null;
  }

  public String lookup(byte[] testCommand, Tester tester, SyncPortReader.Tester dataReceivedTester) {
    for (String device: SerialPortList.getPortNames()) {
      if (device.startsWith("/dev/tty")) {
        if (log.isInfoEnabled()) {
          log.info("trying device: "+device);
        }
        try (SyncPortReader reader = new SyncPortReader(device, 1000, 0, dataReceivedTester)) {
          byte[] answerBytes = reader.execute(testCommand);
          if (tester.isCorrect(answerBytes)) return device;
        } catch (Exception e) {
          log.info("device "+device+" exception: ", e);
        }
      }
    }
    return null;
  }

  public interface Tester {
    boolean isCorrect(byte[] response);
  }
}
