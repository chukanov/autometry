package ru.autometry.obd.processing;

import jssc.*;
import ru.autometry.obd.commands.Answer;
import ru.autometry.obd.commands.Command;
import ru.autometry.obd.commands.Response;
import ru.autometry.obd.commands.adaptor.Adaptor;
import ru.autometry.obd.commands.listener.Listener;
import ru.autometry.obd.config.Config;
import ru.autometry.utils.common.ByteUtils;
import ru.autometry.utils.common.InitUtils;
import ru.autometry.utils.common.Initable;
import ru.autometry.utils.common.InitializationException;
import ru.autometry.utils.processing.BlankSpeedShaper;
import ru.autometry.utils.processing.FastSpeedShaper;
import ru.autometry.utils.processing.SpeedShaper;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jeck on 13/08/14
 */
public class OBDProcessor implements CommandProcessor, CommandDispatcher, SerialPortEventListener, Initable {

  private Config config;

  private SerialPort port;
  private SpeedShaper shaper;

  private Queue<Command> commandsQueue;

  private Command currentCommand;
  private byte[] buffer = {}; //todo replace with ByteBuffer

  private List<Listener> listeners = new ArrayList<Listener>();
  private Lock lock = new ReentrantLock();

  public OBDProcessor(Config config) {
    this.config = config;
  }

  @Override
  public Queue<Command> getCommandQueue() {
    return commandsQueue;
  }

  @Override
  public void serialEvent(SerialPortEvent serialPortEvent) {
    if (serialPortEvent.isRXCHAR()) {
      Response response;
      try {
        lock.lock();
        byte[] out = port.readBytes();
        int currentCommandLength = currentCommand.getBytes().length;
        buffer = ByteUtils.append(buffer, out);
        if (buffer.length <= currentCommandLength + 2) {
          return; ////wait for data
        } else {
          int responseSize = buffer[currentCommandLength + 1];
          if (buffer.length < currentCommandLength + responseSize) {
            return; //wait for data
          } else { //we've got all the response
            response = currentCommand.parseResponse(buffer, new Date());
            for (Answer answer : response.getAnswers()) {
              for (Adaptor adaptor : config.getAdaptors(answer.getId())) {
                adaptor.adapt(answer);
              }
            }
            buffer = new byte[]{};
          }
        }
      } catch (Exception e) {
        System.out.println("Error in receiving");
        e.printStackTrace();
        for (Listener listener : listeners) {
          listener.onError(e, currentCommand, this);
        }

        //todo log
        return;
      } finally {
        lock.unlock();
      }
      process();
      for (Listener listener : listeners) {
        listener.onResponse(response, this);
      }
    }
  }

  public void process() {
    Command command;
    try {
      command = commandsQueue.remove();
    } catch (NoSuchElementException e) {
      System.out.println("Nothing to do");
      //todo log
      return;
    }
    this.currentCommand = command;
    try {
      byte[] bytes = command.getBytes();
      lock.lock();
      port.writeBytes(bytes);
      for (Listener listener : listeners) {
        listener.onRequest(command, this);
      }
    } catch (SerialPortException e) {
      System.out.println("Error in writing");
      e.printStackTrace();
      for (Listener listener : listeners) {
        listener.onError(e, command, this);
      }
      //todo log
    } finally {
      lock.unlock();
      this.commandsQueue.add(command);
      try {
        shaper.request(); //sleep
      } catch (InterruptedException e) {
        //todo log
        //e.printStackTrace();
      }
    }
  }

  @Override
  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  @Override
  public void init(Properties config) throws Exception {
    System.out.println(Arrays.toString(SerialPortList.getPortNames()));
    String portName = InitUtils.getString("port.name", null, config);
    if (portName == null) {
      String portNamesExclude = InitUtils.getString("port.name.exclude", null, config);
      Set<String> excludePorts = new HashSet<>();
      if (portNamesExclude != null) {
        Collections.addAll(excludePorts, portNamesExclude.split(" "));
      }
      for (String name : SerialPortList.getPortNames()) {
        if (name.startsWith("/dev/tty") && !excludePorts.contains(name)) {
          portName = name;
          break;
        }
      }
    }
    if (portName == null) throw new InitializationException("Port not found");
    port = new SerialPort(portName);
    port.openPort();
    port.setParams(InitUtils.getInt("port.boud", SerialPort.BAUDRATE_9600, config),
            InitUtils.getInt("port.databits", SerialPort.DATABITS_8, config),
            InitUtils.getInt("port.stopbits", SerialPort.STOPBITS_1, config),
            InitUtils.getInt("port.parity", SerialPort.PARITY_NONE, config));

    port.addEventListener(this);
    commandsQueue = new ConcurrentLinkedQueue<Command>();
    int speed = InitUtils.getInt("speed", 10, config);
    if (speed > 0) {
      shaper = new FastSpeedShaper(speed);
    } else {
      shaper = new BlankSpeedShaper();
    }
    System.out.println("Port "+portName+" opened");
  }
}
