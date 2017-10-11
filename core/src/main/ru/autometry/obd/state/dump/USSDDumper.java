package ru.autometry.obd.state.dump;

import ru.autometry.gsm.GSMModem;
import ru.autometry.obd.state.OBDState;

/**
 * Created by jeck on 03/05/15
 */
public class USSDDumper implements StateDumper {
  private GSMModem sender;
  private String shortcode;

  private StateSerializer<String> serializer = new Base64Serializer();

  public USSDDumper(GSMModem sender, String shortcode) {
    this.sender = sender;
    this.shortcode = shortcode;
  }

  @Override
  public OBDState load(String user) throws Exception {
    String response = sender.sendUSSD("*"+shortcode +"*load#");
    return serializer.load(response);
  }

  @Override
  public void dump(String user, OBDState state) throws Exception {
    String data = serializer.serialize(state);
    sender.sendUSSD("*"+shortcode + "*" + data+"#");
  }
}
