package ru.autometry.obd.state.dump;

import org.apache.commons.io.FileUtils;
import ru.autometry.obd.state.OBDState;

import java.io.File;

/**
 * Created by jeck on 16/04/15
 */
public class FileStateDumper implements StateDumper {
  private StateSerializer<String> serializer;
  private File dir;

  public FileStateDumper(File dir) {
    this(new JsonStringStateSerializer(), dir);
  }

  public FileStateDumper(StateSerializer<String> serializer, File dir) {
    this.serializer = serializer;
    this.dir = dir;
  }

  public void dump(String user, OBDState state) throws Exception {
    String stateString = serializer.serialize(state);
    FileUtils.writeByteArrayToFile(new File(dir, user), stateString.getBytes());
  }

  public OBDState load(String user) throws Exception {
    String stateString = FileUtils.readFileToString(new File(dir, user), "utf-8");
    return serializer.load(stateString);
  }
}
