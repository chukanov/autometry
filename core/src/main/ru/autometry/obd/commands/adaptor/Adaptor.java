package ru.autometry.obd.commands.adaptor;

import ru.autometry.obd.commands.Answer;
import ru.autometry.obd.exception.OBDException;

/**
 * Created by jeck on 13/08/14
 */
public interface Adaptor {
  public void adapt(Answer answer) throws OBDException;
}
