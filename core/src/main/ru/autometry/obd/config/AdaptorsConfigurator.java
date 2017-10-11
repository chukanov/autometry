package ru.autometry.obd.config;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import ru.autometry.obd.commands.adaptor.Adaptor;
import ru.autometry.utils.common.Configurator;
import ru.autometry.utils.common.Initable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by jeck on 14/08/14
 */
class AdaptorsConfigurator extends Configurator<List<OBDConfigurator.Info<Adaptor>>> {
  private static final Logger log = Logger.getLogger(AdaptorsConfigurator.class);

  @Override
  public List<OBDConfigurator.Info<Adaptor>> configure(HierarchicalConfiguration config) throws Exception {
    List<OBDConfigurator.Info<Adaptor>> result = new ArrayList<OBDConfigurator.Info<Adaptor>>();
    List confList = config.configurationsAt("adaptors.adaptor");
    for (Object configObj : confList) {
      HierarchicalConfiguration aConfig = (HierarchicalConfiguration) configObj;
      String aClass = aConfig.getString(ITEM_CLASS);
      String onCommands = aConfig.getString("[@answer]", "");
      String notCommands = aConfig.getString("[@not-answer]", "");
      Adaptor adaptor = (Adaptor) Class.forName(aClass).getConstructors()[0].newInstance();
      if (adaptor instanceof Initable) {
        Properties properties = this.getProperties(aConfig);
        ((Initable) adaptor).init(properties);
      }
      OBDConfigurator.Info<Adaptor> ent = new OBDConfigurator.Info<Adaptor>();
      ent.entity = adaptor;
      ent.notCommands = StringUtils.isBlank(notCommands) ? new String[0] : notCommands.split(" ");
      ent.onCommands = StringUtils.isBlank(onCommands) ? new String[0] : onCommands.split(" ");
      Arrays.sort(ent.notCommands);
      Arrays.sort(ent.onCommands);
      result.add(ent);
    }
    return result;
  }

  @Override
  protected Logger getLogger() {
    return log;
  }
}
