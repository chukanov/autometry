package ru.autometry.obd.commands.listener;

import ru.autometry.obd.commands.Command;
import ru.autometry.obd.commands.Response;
import ru.autometry.obd.processing.CommandDispatcher;
import ru.autometry.obd.state.OBDState;
import ru.autometry.obd.state.StateHolder;

import java.util.Date;

/**
 * Created by jeck on 15/04/15
 */
public class OnlineTimeListener implements Listener, StateHolder {
  private OBDState state;
  private long lastTime = -1;

  @Override
  public void onRequest(Command command, CommandDispatcher dispatcher) {

  }

  @Override
  public void onResponse(Response response, CommandDispatcher dispatcher) {
    if (lastTime == -1) {
      lastTime = System.currentTimeMillis();
    } else {
      state.setOnlineTime(System.currentTimeMillis() - lastTime + state.getOnlineTime());
      lastTime = System.currentTimeMillis();
    }
    state.setTime(new Date());
  }

  @Override
  public void onError(Exception e, Command command, CommandDispatcher dispatcher) {

  }

  @Override
  public void setState(OBDState state) {
    this.state = state;
  }
}
