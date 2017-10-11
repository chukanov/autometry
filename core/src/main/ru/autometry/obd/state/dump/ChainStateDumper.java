package ru.autometry.obd.state.dump;

import ru.autometry.obd.state.OBDState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jeck on 27/07/16.
 */
public class ChainStateDumper implements StateDumper {
  private List<StateDumper> stateDumpers = new ArrayList<>();

  public ChainStateDumper(List<StateDumper> stateDumpers) {
    this.stateDumpers = stateDumpers;
  }

  public ChainStateDumper() {
  }

  public void addDumper(StateDumper dumper) {
    stateDumpers.add(dumper);
  }

  @Override
  public OBDState load(String user) throws Exception {
    List<OBDState> result = new ArrayList<>(stateDumpers.size());
    for (StateDumper dumper : stateDumpers) {
      try {
        OBDState state = dumper.load(user);
        if (state != null) result.add(state);
      } catch (Exception e) {
        //todo log
      }
    }
    if (result.size()==0) {
      return new OBDState();
    }
    Collections.sort(result, new Comparator<OBDState>() {
      @Override
      public int compare(OBDState o1, OBDState o2) {
        return ((Long) o1.getOnlineTime()).compareTo(o2.getOnlineTime());
      }
    });
    return result.get(result.size()-1);
  }

  @Override
  public void dump(String user, OBDState state) throws Exception {
    for (StateDumper dumper : stateDumpers) {
      dumper.dump(user, state);
    }
  }
}
