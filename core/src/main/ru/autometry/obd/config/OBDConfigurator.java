package ru.autometry.obd.config;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;
import ru.autometry.obd.commands.Command;
import ru.autometry.obd.commands.adaptor.Adaptor;
import ru.autometry.obd.commands.listener.Listener;
import ru.autometry.obd.state.OBDMomentum;
import ru.autometry.obd.state.OBDState;
import ru.autometry.utils.common.Configurator;

import java.util.*;


/**
 * Created by jeck on 13/08/14
 */
public class OBDConfigurator extends Configurator<Config> {

  private static final Logger log = Logger.getLogger(OBDConfigurator.class);

  private Configurator<LinkedHashMap<String, Command>> commandsConfigurator = new CommandListConfigurator();
  private Configurator<List<OBDConfigurator.Info<Adaptor>>> adaptorsConfigurator = new AdaptorsConfigurator();


  private OBDMomentum momentum;
  private OBDState state;

  public OBDConfigurator(OBDMomentum momentum, OBDState state) {
    this.momentum = momentum;
    this.state = state;
  }

  @Override
  public Config configure(HierarchicalConfiguration config) throws Exception {
    OBDConfig conf = new OBDConfig();

    Configurator<List<Listener>> listenersConfigurator = new ListenersConfigurator(momentum, state);

    String id = config.getString("[@id]");
    Properties properties = this.getProperties(config.configurationAt("properties"));
    LinkedHashMap<String, Command> commands = commandsConfigurator.configure(config);
    List<OBDConfigurator.Info<Adaptor>> adaptors = adaptorsConfigurator.configure(config);
    List<Listener> listeners = listenersConfigurator.configure(config);

    conf.id = id;
    conf.properties = properties;
    conf.listeners = listeners;
    conf.commands = commands;
    for (Command c : commands.values()) {
      for (String answerId : c.getPossibleAnswers()) {
        List<Adaptor> commandAdaptors = new ArrayList<Adaptor>();
        for (OBDConfigurator.Info<Adaptor> info : adaptors) {
          if (info.match(answerId)) {
            commandAdaptors.add(info.entity);
          }
        }
        conf.adaptorMap.put(answerId, commandAdaptors);
      }
    }
    return conf;
  }

  @Override
  protected Logger getLogger() {
    return log;
  }

  public static class OBDConfig implements Config {
    String id;
    Properties properties;
    LinkedHashMap<String, Command> commands;
    List<Listener> listeners;
    private Map<String, List<Adaptor>> adaptorMap = new HashMap<String, List<Adaptor>>();

    public LinkedHashMap<String, Command> getCommandsMap() {
      return commands;
    }

    @Override
    public String getId() {
      return id;
    }

    @Override
    public Properties getProperties() {
      return properties;
    }

    @Override
    public List<Command> getCommands() {
      return new ArrayList<Command>(commands.values());
    }

    @Override
    public List<Adaptor> getAdaptors(String answer) {
      List<Adaptor> result = adaptorMap.get(answer);
      if (result == null) {
        result = Collections.emptyList();
      }
      return result;
    }

    @Override
    public List<Listener> getListeners() {
      return listeners;
    }
  }

  public static class Info<T> {
    public T entity;
    public String[] onCommands;
    public String[] notCommands;

    public boolean match(String command) {
      if (onCommands.length == 0) {
        return Arrays.binarySearch(notCommands, command) < 0;
      } else {
        return Arrays.binarySearch(onCommands, command) >= 0;
      }
    }
  }
}
