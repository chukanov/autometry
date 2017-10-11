package ru.autometry.obd.commands;

import ru.autometry.obd.exception.OBDException;
import ru.autometry.obd.utils.OBDHelper;
import ru.autometry.utils.common.InitUtils;
import ru.autometry.utils.common.Initable;

import java.util.*;

/**
 * Created by jeck on 13/08/14
 */
public class OBD1Command implements Command, Initable {
  protected byte[] bytes;
  protected String id;
  protected int length;
  protected Map<String, AnswerConf> answersConf;
  public OBD1Command(String id) {
    this.id = id;
  }

  public OBD1Command(String id, byte offset, byte length) {
    this.id = id;
    this.bytes = OBDHelper.getBytesFor20H(offset, length);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public byte[] getBytes() {
    return bytes;
  }

  @Override
  public Response parseResponse(byte[] rawData, Date answerTime) throws OBDException {
    OBD1Response response = new OBD1Response();
    response.setCommand(this);
    response.setResponse(rawData);
    response.setTime(answerTime);

    int commandLen = this.bytes.length;
    //byte[] valueResponse = new byte[rawData.length - commandLen - 3];
    //byte[] valueResponse = new byte[length];
    //System.arraycopy(rawData, commandLen + 2, commandResponse, 0, commandResponse.length);
    //System.arraycopy(rawData, commandLen + 2, valueResponse, 0, length);
    response.answers = new ArrayList<Answer>(answersConf.size());
    for (AnswerConf conf : answersConf.values()) {
      OBD1Answer answer = new OBD1Answer();
      answer.id = conf.id;
      answer.time = answerTime;
      answer.response = response;
      answer.bytes = new byte[conf.length];
      System.arraycopy(rawData, commandLen + 2 + conf.offset, answer.bytes, 0, conf.length);
      answer.setValue(answer.bytes);
      response.answers.add(answer);
    }

    //response.setValue(valueResponse);
    return response;
  }

  @Override
  public Set<String> getPossibleAnswers() {
    return answersConf.keySet();
  }

  @Override
  public void init(Properties config) throws Exception {
    byte offset = InitUtils.getByte("offset", config);
    byte length = InitUtils.getByte("length", (byte) 0x10, config);
    this.bytes = OBDHelper.getBytesFor20H(offset, length);
    Map<String, String> answersConfOffsets = InitUtils.getMapStringsStartsWith("answer.offset.", config);
    answersConf = new HashMap<String, AnswerConf>(answersConfOffsets.size());
    for (Map.Entry<String, String> answerConfObj : answersConfOffsets.entrySet()) {
      AnswerConf conf = new AnswerConf();
      conf.id = answerConfObj.getKey();
      conf.offset = Integer.valueOf(answerConfObj.getValue());
      conf.length = InitUtils.getInt("answer.length." + conf.id, 1, config);
      answersConf.put(conf.id, conf);
    }
    //this.length = InitUtils.getInt("length", 1, config);
    this.length = length;
  }

  /**
   * Created by jeck on 17/08/14
   */
  static class OBD1Response implements Response {
    Collection<Answer> answers;
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private byte[] response;
    private Date time;
    private Command command;

    public void setResponse(byte[] response) {
      this.response = response;
    }

    @Override
    public Map<String, Object> getAttributes() {
      return attributes;
    }

    @Override
    public byte[] getRawResponse() {
      return response;
    }

    @Override
    public Date getTime() {
      return time;
    }

    public void setTime(Date time) {
      this.time = time;
    }

    @Override
    public Command getCommand() {
      return command;
    }

    public void setCommand(Command command) {
      this.command = command;
    }

    @Override
    public byte[] getBytes() {
      return response;
    }

    @Override
    public Collection<Answer> getAnswers() {
      return answers;
    }
  }

  private static class OBD1Answer implements Answer {
    private String id;
    private Response response;
    private byte[] bytes;
    private Date time;
    private Object value;

    @Override
    public String toString() {
      return "{" +
              "id='" + id + '\'' +
              ", value=" + value +
              ", time=" + time +
              '}';
    }

    @Override
    public String getId() {
      return id;
    }

    @Override
    public Response getResponse() {
      return response;
    }

    @Override
    public byte[] getBytes() {
      return bytes;
    }

    @Override
    public Date getTime() {
      return time;
    }

    @Override
    public Object getValue() {
      return value;
    }

    @Override
    public void setValue(Object obj) {
      this.value = obj;
    }
  }

  private static class AnswerConf {
    private String id;
    private int offset;
    private int length;
  }
}
