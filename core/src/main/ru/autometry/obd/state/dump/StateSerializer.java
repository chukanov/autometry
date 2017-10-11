package ru.autometry.obd.state.dump;

import ru.autometry.obd.state.OBDState;

/**
 * Created by jeck on 14/09/14
 */
public interface StateSerializer<T> {
  public T serialize(OBDState state) throws Exception;

  public OBDState load(T t) throws Exception;
}
