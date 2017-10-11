package ru.autometry.gsm;

/**
 * Created by jeck on 30/04/15
 */

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.ajwcc.pduUtils.gsm3040.Pdu;
import org.ajwcc.pduUtils.gsm3040.PduParser;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import ru.autometry.obd.state.location.GSMLocation;
import ru.autometry.utils.common.ByteUtils;
import ru.autometry.utils.common.NamedThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GSMModem {
  private static final Logger log = Logger.getLogger(GSMModem.class);

  public final static char EOL = 0x0D; //End of line
  public final static char CTRL_Z = 26; //End of message

  private final Object portReadMonitor = new Object();
  private SerialPort port;
  private String portName;

  private String response;

  private List<SMSListener> smsListeners = new ArrayList<>(2);
  private ScheduledExecutorService service;

  public GSMModem(String portName) {
    this.portName = portName;
    service = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("gsm-sms-check."));
  }

  public synchronized void start(long smsCheckdelay) throws SerialPortException {
    port = new SerialPort(portName);
    port.openPort();
    port.setParams(SerialPort.BAUDRATE_9600,
            SerialPort.DATABITS_8,
            SerialPort.STOPBITS_1,
            SerialPort.PARITY_NONE);
    port.addEventListener(new PortListener(port), SerialPort.MASK_RXCHAR);
    service.scheduleWithFixedDelay(new Runnable() {
      @Override
      public void run() {
        try {
          readMessages();
        } catch (Exception e) {
          log.warn("Unable to read message", e);
        }
      }
    }, 0, smsCheckdelay, TimeUnit.MILLISECONDS);
  }

  public void addListener(SMSListener listener) {
    smsListeners.add(listener);
  }

  public synchronized void destroy() {
    try {
      port.closePort();
    } catch (SerialPortException e) {
      log.warn("unable to close modem port", e);
    }
    service.shutdown();
  }

  private synchronized void readMessages() throws Exception {
    new Command(port).execute("AT+CMGL=4" + EOL);
  }

  public synchronized void sendSMS(String sms, String toPhone) throws Exception {
    if (port==null) throw new Exception("usage before start");
      new Command(port).execute("AT+CMGF=0" + EOL); //switch modem to SMS mode
      String message = "0011000B91" + GSMUtils.encodePhone(toPhone) + "0008A7" + GSMUtils.encodeUSC2(sms);
      new Command(port).execute("AT+CMGS=" + GSMUtils.getMessageLength(message) + EOL);
      new Command(port).execute(message + CTRL_Z);
  }

  public synchronized String sendUSSD(String shortcode) throws Exception {
    if (port==null) throw new Exception("usage before start");
      new Command(port).execute("AT^USSDMODE=0" + EOL); //switch modem to USSD mode
      final String[] ussdResponse = new String[1];
      new Command(port) {
        @Override
        protected void read(String response) throws Exception {
          ussdResponse[0] = response;
        }
      }.execute("AT+CUSD=1,\"" + shortcode + "\",15" + EOL);
      if ("OK".equals(ussdResponse[0])) {
        String result = null;
        long startTime = System.currentTimeMillis();
        synchronized (portReadMonitor) {
          boolean mustStop;
          do {
            try {
              portReadMonitor.wait(100);
            } catch (InterruptedException e) {
              log.warn("Interrupter portReadMonitor", e);
            }
            long time = System.currentTimeMillis() - startTime;
            result = response;
            mustStop = ((response != null &&(response.startsWith("+CUSD: 0") || response.startsWith("+CUSD: 1"))) || time >= 10000);
          } while (!mustStop);
          if (result!=null) {
            int qPos = result.lastIndexOf("\"");
            int encodingPos = Integer.parseInt(result.substring(qPos+2));
            String text = result.substring("+CUSD: 0".length()+2,qPos);
            if (encodingPos == 72) {
              byte[] data = ByteUtils.hex2byte(text);
              return new String(data, "UTF-16");
            } else {
              return text;
            }
          } else {
            throw new Exception("Unable to send USSD command: "+shortcode+" No response");
          }
        }
      } else {
        throw new Exception("Unable to send USSD command: "+shortcode+" "+ussdResponse[0]);
      }
  }

  public synchronized GSMLocation location() throws Exception {
    if (port==null) throw new Exception("usage before start");
    final GSMLocation location = new GSMLocation();
      new Command(port) {
        @Override
        protected void read(String response) throws Exception {
          try {
            String data = response.split("\"")[1];
            location.setMcc(Short.parseShort(data.substring(0, 3)));
            location.setMnc(Short.parseShort(data.substring(3)));
          } catch (Throwable t) {
            location.setMcc((short) 250);
            location.setMnc((short) 1);
          }
        }
      }.execute("AT+COPS?" + EOL); //get NET id
      new Command(port).execute("AT+CREG=2" + EOL); //switch to diag mode
      new Command(port) {
        @Override
        protected void read(String response) throws Exception {
          try {
            String[] dataArray = response.split("\"");
            location.setLac(Integer.parseInt(dataArray[1], 16));
            location.setCid(Integer.parseInt(dataArray[3], 16));
          } catch (Throwable t) {
            location.setMcc((short) 0);
            location.setMnc((short) 0);
            location.setLac(0);
            location.setCid(0);
          }
        }
      }.execute("AT+CREG?" + EOL); //ask for lac and cellId
    return location;
  }

  private class Command {
    private SerialPort port;

    protected Command(SerialPort port) {
      this.port = port;
    }

    protected void read(String response) throws Exception {
      //by default do nothing
    }

    void execute(String command) throws Exception {
      if (log.isDebugEnabled()) {
        log.debug("GSM << " + command);
      }
      port.purgePort(SerialPort.PURGE_RXCLEAR | SerialPort.PURGE_TXCLEAR);
      port.writeString(command);
      long startTime = System.currentTimeMillis();
      response = null;
      synchronized (portReadMonitor) {
        boolean mustStop;
        do {
          try {
            portReadMonitor.wait(10);
          } catch (InterruptedException e) {
            log.warn("Interrupter portReadMonitor", e);
          }
          long time = System.currentTimeMillis() - startTime;
          mustStop = (response != null && response.endsWith("\nOK\r\n")) || time >= 500;
        } while (!mustStop);
        String result = response;
        response = null;

        read(result);
      }
    }
  }


  private void processIncomingMessages(String message){
  /*
   String looks like
   +CMGL: 0,1,,160
07919731899689F3400ED0C2303BEC1E971B0008617092028080428C05000355020104110430043B0430043D0441003A003500340039002C00300031044000200421043B0443044804300439044204350020043B044E04310438043C0443044E0020043C04430437044B043A0443002004320020043F04400438043B043E04360435043D04380438002000AB041C042204210020004D007500730069006300BB0021002000370020
   */
    String[] data = StringUtils.splitByWholeSeparator(message, "+CMGL: ");
    for (String msgDataString: data) {
      String msgData = msgDataString;
      String[] dataArray = StringUtils.splitByWholeSeparator(msgDataString,"\r\n");
      int indexMessage = 0;
      if (dataArray.length >= 2) {
        String[] headDataArray = StringUtils.splitByWholeSeparator(dataArray[0], ",");
        indexMessage = Integer.parseInt(headDataArray[0]);
        msgData = dataArray[1];
      }
      msgData = StringUtils.trim(msgData);
      try {
        Pdu pdu = new PduParser().parsePdu(msgData);
        boolean removeMessage = false;
        for (SMSListener listener: smsListeners) {
          removeMessage = listener.receive(pdu.getDecodedText(), pdu.getAddress(), this);
        }
        if (removeMessage) {
          this.removeMessage(indexMessage);
        }
      } catch (Throwable t) {
        log.warn("Error on response: " + msgDataString, t);
      }
    }
  }

  private void removeMessage(int index) throws Exception {
    new Command(port).execute("AT+CMGD="+index+EOL);
  }


  private class PortListener implements SerialPortEventListener {
    private SerialPort port;

    private PortListener(SerialPort port) {
      this.port = port;
    }

    public void serialEvent(SerialPortEvent event) {
      if (event.isRXCHAR() && event.getEventValue() > 0) {
        try {
          synchronized (portReadMonitor) {
            response = StringUtils.trim(port.readString(event.getEventValue()));
            if (response.startsWith("+CMGL")) {
              processIncomingMessages(response);
            }
            System.out.println(">> "+response);
            if (log.isDebugEnabled()) {
              log.debug("GSM >> " + response);
            }
          }
        } catch (SerialPortException ex) {
          log.warn("unable to close modem port", ex);
        }
      }
    }
  }
}