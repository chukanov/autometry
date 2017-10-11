package ru.autometry.obd.commands.listener;

import ru.autometry.obd.commands.Command;
import ru.autometry.obd.commands.Response;
import ru.autometry.obd.processing.CommandDispatcher;
import ru.autometry.obd.state.OBDState;
import ru.autometry.obd.state.StateHolder;

/**
 * Created by jeck on 02/11/15
 */
public class ErrorsListener implements StateHolder, Listener {
  private OBDState state;

  @Override
  public void onRequest(Command command, CommandDispatcher dispatcher) {

  }

  @Override
  public void onResponse(Response response, CommandDispatcher dispatcher) {

  }

  @Override
  public void onError(Exception e, Command command, CommandDispatcher dispatcher) {

  }

  @Override
  public void setState(OBDState state) {
    this.state = state;
  }
}
