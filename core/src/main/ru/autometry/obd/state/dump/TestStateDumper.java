package ru.autometry.obd.state.dump;

import ru.autometry.obd.state.OBDState;

/**
 * Created by jeck on 31/08/16.
 */
public class TestStateDumper implements StateDumper {
  private OBDState state;
  @Override
  public OBDState load(String user) throws Exception {
    return state;
  }

  @Override
  public String toString() {
    return "TestStateDumper{" +
            "state=" + state +
            '}';
  }

  @Override
  public void dump(String user, OBDState state) throws Exception {
    this.state = state;
  }
}
