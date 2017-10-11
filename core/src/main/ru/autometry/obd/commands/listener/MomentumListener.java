package ru.autometry.obd.commands.listener;

import ru.autometry.obd.commands.Answer;
import ru.autometry.obd.commands.Command;
import ru.autometry.obd.commands.Response;
import ru.autometry.obd.processing.CommandDispatcher;
import ru.autometry.obd.state.MomentumHolder;
import ru.autometry.obd.state.OBDMomentum;
import ru.autometry.obd.state.OBDState;
import ru.autometry.obd.state.StateHolder;
import ru.autometry.utils.common.InitUtils;
import ru.autometry.utils.common.Initable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

/**
 * Created by jeck on 18/09/14
 */
public class MomentumListener implements Listener, MomentumHolder, StateHolder, Initable {
  private OBDMomentum momentum;
  private OBDState state;
  private Map<String, String> reflectionMomentum;

  @Override
  public void onRequest(Command command, CommandDispatcher dispatcher) {
  }

  @Override
  public void onResponse(Response response, CommandDispatcher dispatcher) {
    for (Answer answer : response.getAnswers()) {
      for (Map.Entry<String, String> entry : reflectionMomentum.entrySet()) {
        if (answer.getId().equals(entry.getValue())) {
          for (Method method : OBDMomentum.class.getMethods()) {
            if (method.getName().equals("set" + entry.getKey())) {
              try {
                method.invoke(momentum, answer.getValue());
              } catch (Exception e) {
                e.printStackTrace();
                //todo log
              }
            }
          }
        }
      }
    }
    OBDState prevState = momentum.getCurrentState().clone();
    momentum.setPreviousState(prevState);
  }

  @Override
  public void onError(Exception e, Command command, CommandDispatcher dispatcher) {

  }

  @Override
  public void setMomentum(OBDMomentum momentum) {
    this.momentum = momentum;
  }

  @Override
  public void init(Properties config) throws Exception {
    reflectionMomentum = InitUtils.getMapStringsStartsWith("set", config);
  }

  @Override
  public void setState(OBDState state) {
    this.state = state;
  }
}
