package ru.autometry.obd.commands;

import java.util.Date;

/**
 * Created by jeck on 17/08/14
 */
public interface Answer {
  public String getId();

  public Response getResponse();

  public byte[] getBytes();

  public Date getTime();

  public Object getValue();

  public void setValue(Object obj);
}
