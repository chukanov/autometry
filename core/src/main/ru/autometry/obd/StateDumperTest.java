package ru.autometry.obd;

import ru.autometry.obd.state.OBDState;
import ru.autometry.obd.state.dump.ChainStateDumper;
import ru.autometry.obd.state.dump.StateDumper;
import ru.autometry.obd.state.dump.TestStateDumper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by jeck on 31/08/16.
 */
public class StateDumperTest {
  public static void main(String[] args) throws Exception {
    List<StateDumper> stateDumpers = new ArrayList<>();
    OBDState state;
    for(int i=0; i<10; i++) {
      state = new OBDState();
      state.setTime(new Date());
      state.setDistance(1000*Math.random());
      state.setSessionId(i);
      state.setErrors(new String[]{});
      state.setLiters(10*Math.random());
      state.setOnlineTime((long) (System.currentTimeMillis()*Math.random()));
      state.setRevolutions((long) (10000000*Math.random()));
      state.setStopTime((long) (System.currentTimeMillis()*Math.random()));
      StateDumper dumper = new TestStateDumper();
      dumper.dump("", state);
      stateDumpers.add(dumper);
    }
    Collections.shuffle(stateDumpers);
    System.out.println("List: "+stateDumpers);

    StateDumper dumper = new ChainStateDumper(stateDumpers);
    System.out.println("Result: "+dumper.load(""));

  }
}
