package ru.autometry.obd.state.dump;

import ru.autometry.obd.state.OBDState;

/**
 * Created by jeck on 27/04/15
 */
public interface StateDumper {
  public OBDState load(String user) throws Exception;

  public void dump(String user, OBDState state) throws Exception;
}
