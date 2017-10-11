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

import java.util.Properties;

/**
 * Created by jeck on 18/09/14
 */
public class FuelLitersListener implements Listener, StateHolder, MomentumHolder, Initable {
  private static final int cylindersCount = 4; //todo move to cfg
  double correctionLiters = 0.0d; //todo move to cfg
  double startLiters;
  private OBDState state;
  private OBDMomentum momentum;
  private long lastTime = 0;
  private double totalInjTime;
  private String injTimeId;
  private FuelInjFlowRate injFlowRate = FuelInjFlowRate.FR240cc; //todo move to cfg

  public FuelLitersListener() {
  }

  public FuelLitersListener(String injTimeId) {
    this.injTimeId = injTimeId;
  }

  @Override
  public void onRequest(Command command, CommandDispatcher dispatcher) {

  }

  @Override
  public void onResponse(Response response, CommandDispatcher dispatcher) {
    for (Answer answer : response.getAnswers()) {
      if (answer.getId().equals(injTimeId)) {
        if (lastTime == 0) {
          lastTime = answer.getTime().getTime();
          startLiters = state.getLiters();
          return;
        }
        int rpm = momentum.getRpm();
        double injTime = (Double) answer.getValue();
        if (rpm <= 0) return;
        double momentumTimeSpan = (answer.getTime().getTime() - lastTime) / 1000d;
        double injCount = momentumTimeSpan * (rpm / 60f) * (cylindersCount / 2f);
        double momentumInjTime = injTime * injCount / 1000d;
        totalInjTime += momentumInjTime;
        state.setLiters(startLiters + calcLiters(totalInjTime));
        lastTime = answer.getTime().getTime();
      }
    }
  }

  private double calcLiters(double injTime) {
    double tmp = injTime / 60d * injFlowRate.value / 1000d;
    return tmp + tmp * correctionLiters;
  }

  @Override
  public void onError(Exception e, Command command, CommandDispatcher dispatcher) {

  }

  @Override
  public void setState(OBDState state) {
    this.state = state;
  }

  @Override
  public void setMomentum(OBDMomentum momentum) {
    this.momentum = momentum;
  }

  @Override
  public void init(Properties config) throws Exception {
    injTimeId = InitUtils.getString("injtime.id", config);
  }

  public enum FuelInjFlowRate {
    FR180cc(180),
    FR235cc(235),
    FR240cc(240),
    FR275cc(270),
    FR325cc(325);

    public int value;

    FuelInjFlowRate(int value) {
      this.value = value;
    }
  }
}
