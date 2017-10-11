package ru.autometry.obd.config;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;
import ru.autometry.obd.commands.listener.Listener;
import ru.autometry.obd.state.MomentumHolder;
import ru.autometry.obd.state.OBDMomentum;
import ru.autometry.obd.state.OBDState;
import ru.autometry.obd.state.StateHolder;
import ru.autometry.utils.common.Configurator;
import ru.autometry.utils.common.Initable;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by jeck on 14/08/14
 */
class ListenersConfigurator extends Configurator<List<Listener>> {
  private static final Logger log = Logger.getLogger(ListenersConfigurator.class);

  private OBDMomentum momentum;
  private OBDState state;

  ListenersConfigurator(OBDMomentum momentum, OBDState state) {
    this.momentum = momentum;
    this.state = state;
  }

  @Override
  public List<Listener> configure(HierarchicalConfiguration config) throws Exception {
    List<Listener> result = new ArrayList<Listener>();
    List confList = config.configurationsAt("listeners.listener");
    for (Object configObj : confList) {
      HierarchicalConfiguration aConfig = (HierarchicalConfiguration) configObj;
      String aClass = aConfig.getString(ITEM_CLASS);
      Listener listener = (Listener) Class.forName(aClass).getConstructors()[0].newInstance();
      if (listener instanceof MomentumHolder) {
        ((MomentumHolder) listener).setMomentum(momentum);
      }
      if (listener instanceof StateHolder) {
        ((StateHolder) listener).setState(state);
      }

      if (listener instanceof Initable) {
        Properties properties = this.getProperties(aConfig);
        ((Initable) listener).init(properties);
      }
      result.add(listener);
    }
    return result;
  }

  @Override
  protected Logger getLogger() {
    return log;
  }
}
