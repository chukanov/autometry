package ru.autometry.obd.processing;

import ru.autometry.obd.commands.Answer;
import ru.autometry.obd.commands.Command;
import ru.autometry.obd.commands.Response;
import ru.autometry.obd.commands.adaptor.Adaptor;
import ru.autometry.obd.commands.listener.Listener;
import ru.autometry.obd.config.Config;
import ru.autometry.utils.common.InitUtils;
import ru.autometry.utils.common.Initable;
import ru.autometry.utils.serial.SyncPortReader;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by jeck on 24/08/16.
 */
public class OBDSyncProcessor implements CommandProcessor, CommandDispatcher, Initable {
  private SyncPortReader reader;
  private Queue<Command> commandsQueue;
  private List<Listener> listeners = new ArrayList<Listener>();
  private Config config;

  private String portName;
  private long portTimeout;
  private int rps;

  public OBDSyncProcessor(String portName, long portTimeout, int rps, Config config) {
    this.config = config;
    this.portName = portName;
    this.portTimeout = portTimeout;
    this.rps = rps;
  }

  @Override
  public void init(Properties config) throws Exception {
    reader = new SyncPortReader(portName, portTimeout, rps, new SyncPortReader.Tester() {
      @Override
      public boolean isDataReceived(byte[] command, byte[] buffer) {
        if (buffer.length > command.length + 2) {
          int responseSize = buffer[command.length + 1];
          return buffer.length >= command.length + responseSize;
        }
        return false;
      }
    });

    commandsQueue = new ConcurrentLinkedQueue<>();
    System.out.println("Port "+portName+" opened");
  }

  @Override
  public void process() {
    while (commandsQueue.size() > 0) {
      Command command = commandsQueue.remove();
      try {
        for (Listener listener : listeners) {
          listener.onRequest(command, this);
        }
        byte[] data = reader.execute(command.getBytes());
        Response response = command.parseResponse(data, new Date());
        for (Answer answer : response.getAnswers()) {
          for (Adaptor adaptor : config.getAdaptors(answer.getId())) {
            adaptor.adapt(answer);
          }
        }
        for (Listener listener : listeners) {
          listener.onResponse(response, this);
        }
      } catch (Exception e) {
        System.out.println("Error");
        e.printStackTrace();
        for (Listener listener : listeners) {
          listener.onError(e, command, this);
        }
        try {
          reader.restart();
        } catch (Exception e1) {
          e1.printStackTrace();
          //todo log
        }
        //todo log
      } finally {
        this.commandsQueue.add(command);
      }
    }
  }

  @Override
  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  @Override
  public Queue<Command> getCommandQueue() {
    return commandsQueue;
  }
}
