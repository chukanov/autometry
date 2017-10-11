package ru.autometry.obd.commands.listener;

import ru.autometry.obd.commands.Command;
import ru.autometry.obd.commands.Response;
import ru.autometry.obd.processing.CommandDispatcher;
import ru.autometry.obd.state.MomentumHolder;
import ru.autometry.obd.state.OBDMomentum;

/**
 * Created by jeck on 02/11/15
 */
public abstract class ShutdownProcessor implements Listener {
  private boolean down = false;

  protected abstract void processShutdown();

  public ShutdownProcessor(OBDMomentum momentum) {
    this.momentum = momentum;
  }

  private OBDMomentum momentum;

  @Override
  public void onRequest(Command command, CommandDispatcher dispatcher) {

  }

  @Override
  public void onResponse(Response response, CommandDispatcher dispatcher) {
    if (down) {
      if (momentum.getRpm() != 0) {
        down = false;
      }
    } else {
      if (momentum.getRpm() == 0) {
        down = true;
        this.processShutdown();
      }
    }
  }

  @Override
  public void onError(Exception e, Command command, CommandDispatcher dispatcher) {

  }
}
