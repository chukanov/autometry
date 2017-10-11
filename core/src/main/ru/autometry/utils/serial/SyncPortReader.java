package ru.autometry.utils.serial;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.apache.log4j.Logger;
import ru.autometry.utils.common.ByteUtils;
import ru.autometry.utils.processing.BlankSpeedShaper;
import ru.autometry.utils.processing.FastSpeedShaper;
import ru.autometry.utils.processing.SpeedShaper;

import java.io.Closeable;
import java.io.IOException;


/**
 * Created by jeck on 24/08/16.
 *
 */
public class SyncPortReader implements SerialPortEventListener, Closeable {
  private static Logger log = Logger.getLogger(SyncPortReader.class);

  private String portName;

  private final Object readMonitor = new Object();

  private SerialPort port;
  private long timeout;
  private SpeedShaper shaper;

  private byte[] buffer = {};

  private Tester tester;

  public SyncPortReader(String portName, long timeout, int rps, Tester tester) throws SerialPortException {
    this.portName = portName;
    this.openPort(portName);
    this.timeout = timeout;
    if (rps > 0) {
      shaper = new FastSpeedShaper(rps);
    } else {
      shaper = new BlankSpeedShaper();
    }
    this.tester = tester;
  }

  public SyncPortReader(String portName, long timeout, int rps) throws SerialPortException {
    this(portName, timeout, rps,  new Tester() {
      @Override
      public boolean isDataReceived(byte[] request, byte[] buffer) {
        return buffer.length > 0;
      }
    });
  }

  public synchronized void restart() throws Exception {
    if (port.isOpened()) {
      port.removeEventListener();
      port.closePort();
    }
    this.openPort(portName);
  }

  private void openPort(String portName) throws SerialPortException {
    port = new SerialPort(portName);
    port.openPort();
    port.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
    port.addEventListener(this);
  }

  public synchronized byte[] execute(byte[] req) throws Exception {
    shaper.request();
    this.buffer = new byte[0];
    port.writeBytes(req);
    long startTime = System.currentTimeMillis();
    synchronized (readMonitor) {
      do {
        try {
          readMonitor.wait(10);
        } catch (InterruptedException e) {
          log.warn("Interrupter portReadMonitor", e);
        }
        long time = System.currentTimeMillis() - startTime;
        if (time >= timeout) {
          throw new Exception("timeout on port "+portName);
        }
      } while (!tester.isDataReceived(req, buffer));
      return buffer;
    }
  }

  @Override
  public void serialEvent(SerialPortEvent serialPortEvent) {
    if (serialPortEvent.isRXCHAR()) {
      try {
        synchronized (readMonitor) {
          byte[] out = port.readBytes();
          buffer = ByteUtils.append(buffer, out);
        }
      } catch (Exception e) {
        log.warn("Error in receiving", e);
      }
    }
  }

  @Override
  public void close() throws IOException {
    try {
      port.closePort();
    } catch (SerialPortException e) {
      throw new IOException("Can't close port", e);
    }
  }

  public interface Tester {
    boolean isDataReceived(byte[] request, byte[] buffer);
  }
}
