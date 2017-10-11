package ru.autometry.obd.commands.listener;

import ru.autometry.obd.commands.Command;
import ru.autometry.obd.commands.Response;
import ru.autometry.obd.processing.CommandDispatcher;

/**
 * Created by jeck on 13/08/14
 */
public interface Listener {
  public void onRequest(Command command, CommandDispatcher dispatcher);

  public void onResponse(Response response, CommandDispatcher dispatcher);

  public void onError(Exception e, Command command, CommandDispatcher dispatcher);
}
