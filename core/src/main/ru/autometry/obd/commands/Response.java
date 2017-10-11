package ru.autometry.obd.commands;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by jeck on 13/08/14
 */
public interface Response {
  public Map<String, Object> getAttributes();

  public byte[] getRawResponse();

  public Date getTime();

  public Command getCommand();

  public byte[] getBytes();

  public Collection<Answer> getAnswers();
}
