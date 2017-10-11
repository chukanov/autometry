package ru.autometry.obd.state.dump;

import ru.autometry.obd.state.OBDState;
import ru.autometry.obd.state.location.GSMLocation;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by jeck on 14/09/14
 */
public class JsonStringStateSerializer implements StateSerializer<String> {
  public static final String REVOLUTIONS = "r";
  public static final String DISTANCE = "d";
  public static final String LITERS = "l";
  public static final String TIME = "t";
  public static final String STOP_TIME = "st";
  public static final String ONLINE_TIME = "ot";
  public static final String ERRORS = "e";
  public static final String SESSION = "s";
  public static final String LOCATION = "loc";
  public static final String LOCATION_MCC = "mcc";
  public static final String LOCATION_MNC = "mnc";
  public static final String LOCATION_LAC = "lac";
  public static final String LOCATION_CID = "cid";

  @Override
  public String serialize(OBDState state) {
    JsonObject json = Json.createObjectBuilder().
            add(REVOLUTIONS, state.getRevolutions()).
            add(DISTANCE, state.getDistance()).
            add(LITERS, state.getLiters()).
            add(TIME, state.getTime().getTime()).
            add(STOP_TIME, state.getStopTime()).
            add(ONLINE_TIME, state.getOnlineTime()).
            add(ERRORS, state.getErrors().length).
            add(SESSION, state.getSessionId()).
            add(LOCATION, Json.createObjectBuilder()
                    .add(LOCATION_MCC, state.getLocation().getMcc())
                    .add(LOCATION_MNC, state.getLocation().getMnc())
                    .add(LOCATION_LAC, state.getLocation().getLac())
                    .add(LOCATION_CID, state.getLocation().getCid())
            ).build();
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      JsonWriter writer = Json.createWriter(os);
      writer.write(json);
      writer.close();
      os.flush();
      return new String(os.toByteArray());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        os.close();
      } catch (IOException e) {
        //todo log
      }
    }
    return "{}";
  }

  @Override
  public OBDState load(String jsonString) throws Exception {
    InputStream is = new ByteArrayInputStream(jsonString.getBytes("utf-8"));
    try (JsonReader reader = Json.createReader(is)) {
      JsonObject json = reader.readObject();
      OBDState state = new OBDState();
      state.setRevolutions(json.getJsonNumber(REVOLUTIONS).longValue());
      state.setDistance(json.getJsonNumber(DISTANCE).doubleValue());
      state.setLiters(json.getJsonNumber(LITERS).doubleValue());
      state.setTime(new Date(json.getJsonNumber(TIME).longValue()));
      state.setStopTime(json.getJsonNumber(STOP_TIME).longValue());
      state.setOnlineTime(json.getJsonNumber(ONLINE_TIME).longValue());
      state.setErrors(new String[]{});
      state.setSessionId(json.getJsonNumber(SESSION).intValue());
      JsonObject jLoc = json.getJsonObject(LOCATION);
      GSMLocation location = new GSMLocation();
      try {
        location.setMcc((short) jLoc.getJsonNumber(LOCATION_MCC).intValue());
        location.setMnc((short) jLoc.getJsonNumber(LOCATION_MNC).intValue());
        location.setLac(jLoc.getJsonNumber(LOCATION_LAC).intValue());
        location.setCid(jLoc.getJsonNumber(LOCATION_CID).intValue());
      } catch (Throwable t) {
        //do nothing
      }
      state.setLocation(location);
      return state;
    } finally {
      try {
        is.close();
      } catch (Exception e) {
        //todo log
      }
    }
  }
}
