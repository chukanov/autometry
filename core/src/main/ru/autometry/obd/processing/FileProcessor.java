package ru.autometry.obd.processing;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import ru.autometry.obd.commands.Answer;
import ru.autometry.obd.commands.Command;
import ru.autometry.obd.commands.Response;
import ru.autometry.obd.commands.adaptor.Adaptor;
import ru.autometry.obd.commands.listener.Listener;
import ru.autometry.obd.config.Config;
import ru.autometry.utils.common.ByteUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by jeck on 22/08/14
 */
public class FileProcessor implements CommandProcessor, CommandDispatcher {
  private List<Listener> listeners = new ArrayList<>();
  private File inputFile;
  private Config config;

  public FileProcessor(File inputFile, Config config) {
    this.inputFile = inputFile;
    this.config = config;
  }

  @Override
  public void process() {
    try {
      long prevTime = 0;
      LineIterator it = FileUtils.lineIterator(inputFile);
      while (it.hasNext()) {
        String line = it.next().toString();
        String[] data = StringUtils.splitByWholeSeparator(line, ";");
        long time = Long.valueOf(data[0]);
        String cid = data[1];
        byte[] resp = ByteUtils.hex2byte(data[2]);

        if (prevTime != 0) {
          Thread.sleep(time - prevTime);
        }
        prevTime = time;
        Response response = null;
        for (Command command : config.getCommands()) {
          if (command.getId().equals(cid)) {
            response = command.parseResponse(resp, new Date(time));
            break;
          }
        }
        if (response == null) continue;
        for (Answer answer : response.getAnswers()) {
          for (Adaptor adaptor : config.getAdaptors(answer.getId())) {
            adaptor.adapt(answer);
          }
        }
        for (Listener listener : listeners) {
          listener.onResponse(response, this);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  @Override
  public Queue<Command> getCommandQueue() {
    return new LinkedBlockingQueue<>();
  }

}
