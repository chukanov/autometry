package ru.autometry.obd;

import ru.autometry.gsm.GSMModem;
import ru.autometry.gsm.SMSListener;
import ru.autometry.obd.commands.OBD1Command;
import ru.autometry.obd.commands.listener.DistanceDependsProcessor;
import ru.autometry.obd.commands.listener.Listener;
import ru.autometry.obd.commands.listener.ShutdownProcessor;
import ru.autometry.obd.config.Config;
import ru.autometry.obd.config.OBDConfigurator;
import ru.autometry.obd.processing.CommandProcessor;
import ru.autometry.obd.processing.OBDSyncProcessor;
import ru.autometry.obd.state.dump.JsonStringStateSerializer;
import ru.autometry.obd.state.OBDMomentum;
import ru.autometry.obd.state.OBDState;
import ru.autometry.obd.state.dump.ChainStateDumper;
import ru.autometry.obd.state.dump.FileStateDumper;
import ru.autometry.obd.state.dump.StateDumper;
import ru.autometry.obd.state.dump.USSDDumper;
import ru.autometry.utils.common.*;
import ru.autometry.utils.serial.PortScanner;
import ru.autometry.utils.serial.SyncPortReader;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by jeck on 27/07/16.
 */
public class Worker implements Initable {

  private OBDState state;
  private OBDMomentum momentum;
  private StateDumper dumper;
  private StateDumper localDumper;
  private ScheduledExecutorService localService;
  private long localStateDelay;

  private GSMModem modem;
  private CommandProcessor processor;

  public OBDState getState() {
    return state;
  }

  public OBDMomentum getMomentum() {
    return momentum;
  }

  private String dummyUser = "";

  public void work() throws Exception {
    localService.scheduleWithFixedDelay(new Runnable() {
      @Override
      public void run() {
        try {
          localDumper.dump(dummyUser, state);
        } catch (Exception e) {
          //todo log
        }
      }
    },localStateDelay, localStateDelay, TimeUnit.MILLISECONDS);
    processor.process();
  }

  @Override
  public void init(Properties properties) throws Exception {
    localService = Executors.newScheduledThreadPool(3, new NamedThreadFactory("local.save"));

    localStateDelay = InitUtils.getLong("state.local.delay", 3000, properties);

    String processorRulesFilename = InitUtils.getString("obd.rules.file", properties);

    this.modem = newGSMModem(properties);
    modem.start(InitUtils.getLong("check.incoming-sms.period", 5000, properties));
    this.dumper = newDumper(properties);
    String localStateFilename = InitUtils.getString("state.local.file", properties);
    this.localDumper = new FileStateDumper(new File(localStateFilename));
    ChainStateDumper chainStateDumper = new ChainStateDumper();
    chainStateDumper.addDumper(dumper);
    chainStateDumper.addDumper(localDumper);
    try {
      this.state = chainStateDumper.load(dummyUser);
    } catch (Exception e) {
      this.state = new OBDState();
    }

    this.state.setSessionId(this.state.getSessionId() + 1);
    this.momentum = new OBDMomentum();
    momentum.setCurrentState(state);

    Configurator<Config> commandsConfigurator = new OBDConfigurator(momentum, state);
    Config config = commandsConfigurator.configure(new File(processorRulesFilename));
    processor = newProcessor(config, properties);
    for(Listener listener: config.getListeners()) {
      processor.addListener(listener);
    }
    processor.addListener(new ShutdownProcessor(momentum) {
      @Override
      protected void processShutdown() {
        try {
          state.setLocation(modem.location());
          dumper.dump(dummyUser, state);
        } catch (Exception e) {
          //todo log
        }
      }
    });

    final int distanseThreshold = InitUtils.getInt("dump.every.in_meters", 1000, properties);
    processor.addListener(new DistanceDependsProcessor(distanseThreshold, state) {
      @Override
      protected void process() {
        localService.submit(new Runnable() {
          @Override
          public void run() {
            try {
              state.setLocation(modem.location());
              System.out.println("SENT: "+state);
              dumper.dump(dummyUser, state);
            } catch (Exception e) {
              // TODO: 28/07/16 log
            }
          }
        });
      }
    });
    processor.getCommandQueue().addAll(config.getCommands());

    final String ownerPhone = InitUtils.getString("master.phone", null, properties);
    modem.addListener(new SMSListener() {
      @Override
      public boolean receive(String message, String from, GSMModem modem1) {
        /*if (ownerPhone != null && from.equals(ownerPhone)) {
          try {
            modem1.sendSMS(new JsonStringStateSerializer().serialize(state), from);
          } catch (Exception e) {
            //todo log
          }
        }*/
        return true;
      }
    });

  }

  protected CommandProcessor newProcessor(Config config, Properties properties) throws Exception {
    String portName = InitUtils.getString("obd.port.name", null, properties);
    if (portName == null) {
      PortScanner tester = new PortScanner();
      final OBD1Command command = new OBD1Command("idecu", (byte)118, (byte)10);
      portName = tester.lookup(command.getBytes(), new PortScanner.Tester() {
        @Override
        public boolean isCorrect(byte[] response) {
          System.out.print("--> "+ByteUtils.debugHexString(response));
          if (response.length > command.getBytes().length) {
            for (int i=0; i<command.getBytes().length; i++) {
              if (response[i] != command.getBytes()[i]){
                return false;
              }
            }
            return true;
          }
          return false;
        }
      }, new SyncPortReader.Tester() {
        @Override
        public boolean isDataReceived(byte[] command, byte[] buffer) {
          if (buffer.length > command.length + 2) {
            int responseSize = buffer[command.length + 1];
            return buffer.length >= command.length + responseSize;
          }
          return false;
        }
      });
      System.out.println("OBD port found: "+portName);
      if (portName == null) throw new InitializationException("Unable to find OBD adaptor");
    }
    long timeout = InitUtils.getLong("obd.port.timeout", 1000, properties);
    int rps = InitUtils.getInt("obd.port.speed", 5, properties);
    OBDSyncProcessor processor = new OBDSyncProcessor(portName, timeout, rps, config);
    processor.init(properties);
    return processor;
  }

  protected GSMModem newGSMModem(Properties properties) throws Exception {
    String gsmModemPort = InitUtils.getString("gsm.port.name", null, properties);
    if (gsmModemPort == null) {
      final String gsmModemModel = InitUtils.getString("gsm.model", properties);
      PortScanner tester = new PortScanner();
      gsmModemPort = tester.lookup(("ATI" + GSMModem.EOL).getBytes(), new PortScanner.Tester() {
        @Override
        public boolean isCorrect(byte[] response) {
          try {
            String stringResponse = new String(response, "utf-8");
            if (stringResponse.contains(gsmModemModel)) return true;
          } catch (UnsupportedEncodingException e) {
            return false;
          }
          return false;
        }
      });
      System.out.println("GSM found: "+gsmModemPort);
      if (gsmModemPort == null) throw new InitializationException("Unable to find GSM modem: "+gsmModemModel);
    }
    return new GSMModem(gsmModemPort);
  }

  protected StateDumper newDumper(Properties properties) throws Exception {
/*    String localStateFilename = InitUtils.getString("state.local.file", properties);
    ChainStateDumper dumper = new ChainStateDumper();
    dumper.addDumper();
    dumper.addDumper(new FileStateDumper(new JsonStringStateSerializer(), new File(localStateFilename))); */
    return new USSDDumper(modem, InitUtils.getString("obd.ussd.dumper.ussd", properties));
    //return new FileStateDumper(new JsonStringStateSerializer(), new File(InitUtils.getString("state.local.file", properties)));
  }

}
