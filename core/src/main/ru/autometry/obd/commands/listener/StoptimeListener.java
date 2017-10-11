package ru.autometry.obd.commands.listener;

import ru.autometry.obd.commands.Answer;
import ru.autometry.obd.commands.Command;
import ru.autometry.obd.commands.Response;
import ru.autometry.obd.processing.CommandDispatcher;
import ru.autometry.obd.state.OBDState;
import ru.autometry.obd.state.StateHolder;
import ru.autometry.utils.common.InitUtils;
import ru.autometry.utils.common.Initable;

import java.util.Properties;

/**
 * Created by jeck on 02/12/14
 */
public class StoptimeListener implements Listener, StateHolder, Initable {
  private String speedId;
  private long lastTime = -1;
  private OBDState state;

  public StoptimeListener() {
  }

  public StoptimeListener(String speedId) {
    this.speedId = speedId;
  }

  @Override
  public void onRequest(Command command, CommandDispatcher dispatcher) {
  }

  @Override
  public void onResponse(Response response, CommandDispatcher dispatcher) {
    for (Answer answer : response.getAnswers()) {
      if (answer.getId().equals(speedId)) {
        int speed = (Integer) answer.getValue();
        if (speed == 0) {
          if (lastTime == -1) {
            lastTime = answer.getTime().getTime();
          } else {
            long timeInMs = answer.getTime().getTime() - lastTime;
            state.setStopTime(state.getStopTime() + timeInMs);
            lastTime = answer.getTime().getTime();
          }
        } else {
          lastTime = -1;
        }
      }
    }

  }

  @Override
  public void onError(Exception e, Command command, CommandDispatcher dispatcher) {

  }

  @Override
  public void setState(OBDState state) {
    this.state = state;
  }

  @Override
  public void init(Properties config) throws Exception {
    speedId = InitUtils.getString("speed.id", config);
  }
}
