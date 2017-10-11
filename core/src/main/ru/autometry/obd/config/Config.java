package ru.autometry.obd.config;

import ru.autometry.obd.commands.Command;
import ru.autometry.obd.commands.adaptor.Adaptor;
import ru.autometry.obd.commands.listener.Listener;

import java.util.List;
import java.util.Properties;

/**
 * Created by jeck on 13/08/14
 */
public interface Config {
  public String getId();

  public Properties getProperties();

  public List<Command> getCommands();

  public List<Adaptor> getAdaptors(String answerId);

  public List<Listener> getListeners();
}
