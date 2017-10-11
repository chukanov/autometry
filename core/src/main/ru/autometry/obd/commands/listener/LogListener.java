package ru.autometry.obd.commands.listener;

import ru.autometry.obd.commands.Command;
import ru.autometry.obd.commands.Response;
import ru.autometry.obd.processing.CommandDispatcher;
import ru.autometry.utils.common.ByteUtils;

/**
 * Created by jeck on 13/08/14
 */
public class LogListener implements Listener {
  @Override
  public void onRequest(Command command, CommandDispatcher dispatcher) {
    //System.out.println(System.currentTimeMillis()+":: "+command.getId() + ": " + ByteUtils.debugHexString(command.getBytes()));
  }

  @Override
  public void onResponse(Response response, CommandDispatcher dispatcher) {
    System.out.println(response.getTime().getTime() + ":: " + response.getCommand().getId() + "=" + response.getAnswers() + ", dump=" + ByteUtils.debugHexString(response.getRawResponse()));
  }

  @Override
  public void onError(Exception e, Command command, CommandDispatcher dispatcher) {
    System.out.println("ERROR on " + command.getId() + ", " + e.getLocalizedMessage());
  }
}
