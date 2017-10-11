package ru.autometry.obd.commands;

import ru.autometry.obd.exception.OBDException;

import java.util.Date;
import java.util.Set;

/**
 * Created by jeck on 13/08/14
 */
public interface Command {
  public String getId();

  public byte[] getBytes();

  public Response parseResponse(byte[] answer, Date answerTime) throws OBDException;

  public Set<String> getPossibleAnswers();
}
