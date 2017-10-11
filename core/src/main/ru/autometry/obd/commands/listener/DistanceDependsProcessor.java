package ru.autometry.obd.commands.listener;

import ru.autometry.obd.commands.Command;
import ru.autometry.obd.commands.Response;
import ru.autometry.obd.processing.CommandDispatcher;
import ru.autometry.obd.state.OBDState;
import ru.autometry.obd.state.StateHolder;

/**
 * Created by jeck on 28/07/16.
 */
public abstract class DistanceDependsProcessor implements Listener {
  private double lastDistance;
  private int threshold;
  private OBDState state;

  protected DistanceDependsProcessor(int threshold, OBDState state) {
    this.threshold = threshold;
    this.state = state;
  }

  /**
   * Abstract metod defines action doing one time at getThreshold() meters
   */
  protected abstract void process();

  @Override
  public void onRequest(Command command, CommandDispatcher dispatcher) {
  }

  @Override
  public void onResponse(Response response, CommandDispatcher dispatcher) {
    double currentDistance = state.getDistance();
    int difference = (int) ((currentDistance - lastDistance)*1000);
    //System.out.println("<>"+difference+" NOW: "+state);
    if (difference > threshold) {
      lastDistance = currentDistance;
      this.process();
    }
  }

  @Override
  public void onError(Exception e, Command command, CommandDispatcher dispatcher) {
  }
}
