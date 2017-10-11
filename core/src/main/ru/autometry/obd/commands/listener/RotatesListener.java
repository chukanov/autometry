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
 * Created by jeck on 15/04/15
 */
public class RotatesListener implements Listener, StateHolder, Initable {
  private String rpmId;
  private long lastTime = -1;
  private OBDState state;

  @Override
  public void onRequest(Command command, CommandDispatcher dispatcher) {
  }

  @Override
  public void onResponse(Response response, CommandDispatcher dispatcher) {
    for (Answer answer : response.getAnswers()) {
      if (answer.getId().equals(rpmId)) {
        if (lastTime == -1) {
          lastTime = answer.getTime().getTime();
        } else {
          long timeInMs = answer.getTime().getTime() - lastTime;
          double timeInMinutes = (((double) timeInMs) / 1000d) / 60d;
          int rpm = (Integer) answer.getValue();
          state.setRevolutions((long) (state.getRevolutions() + ((double) rpm) * 1000 * timeInMinutes));
          lastTime = answer.getTime().getTime();
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
    rpmId = InitUtils.getString("rpm.id", config);
  }
}
