package ru.autometry.obd.processing;

import ru.autometry.obd.commands.listener.Listener;

/**
 * Created by jeck on 13/08/14
 */
public interface CommandProcessor extends CommandDispatcher {
  public void process();

  public void addListener(Listener listener);
}
