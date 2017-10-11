package ru.autometry.obd.commands.listener;

import ru.autometry.obd.commands.Command;
import ru.autometry.obd.commands.Response;
import ru.autometry.obd.processing.CommandDispatcher;

/**
 * Created by jeck on 13/08/14
 */
public class RetryListener implements Listener {
  @Override
  public void onRequest(Command command, CommandDispatcher dispatcher) {
    //do nothing
  }

  @Override
  public void onResponse(Response response, CommandDispatcher dispatcher) {
    dispatcher.getCommandQueue().add(response.getCommand());
  }

  @Override
  public void onError(Exception e, Command command, CommandDispatcher dispatcher) {
    dispatcher.getCommandQueue().add(command);
  }
}
