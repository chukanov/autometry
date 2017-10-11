package ru.autometry.obd.config;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;
import ru.autometry.obd.commands.Command;
import ru.autometry.utils.common.Configurator;
import ru.autometry.utils.common.Initable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

/**
 * Created by jeck on 13/08/14
 */
class CommandListConfigurator extends Configurator<LinkedHashMap<String, Command>> {
  private static final Logger log = Logger.getLogger(CommandListConfigurator.class);

  @Override
  public LinkedHashMap<String, Command> configure(HierarchicalConfiguration config) throws Exception {
    LinkedHashMap<String, Command> result = new LinkedHashMap<String, Command>();
    List commandsConfList = config.configurationsAt("commands.command");
    for (Object commandConfigObj : commandsConfList) {
      HierarchicalConfiguration commandConfig = (HierarchicalConfiguration) commandConfigObj;
      String commandClass = commandConfig.getString(ITEM_CLASS);
      String id = commandConfig.getString("[@id]", null);
      Command command = (Command) Class.forName(commandClass).getConstructor(String.class).newInstance(id);
      if (command instanceof Initable) {
        Properties properties = this.getProperties(commandConfig);
        ((Initable) command).init(properties);
      }
      result.put(id, command);
    }
    return result;
  }

  @Override
  protected Logger getLogger() {
    return log;
  }
}
