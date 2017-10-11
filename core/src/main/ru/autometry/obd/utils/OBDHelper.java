package ru.autometry.obd.utils;

/**
 * Created by jeck on 30/06/14
 */
public class OBDHelper {


  public static byte getCheckSum(byte[] data, boolean ignoreLastByte) {
    int res = 0;
    if (data != null && data.length > 0) {
      int cnt = data.length;
      if (ignoreLastByte) {
        cnt--;
      }
      res = 256;
      if (cnt > 0) {
        for (int i = 0; i < cnt; i++) {
          res = res - data[i];
          if (res < 0) {
            res = 256 + res;
          }
        }
      }
    }
    return (byte) res;
  }

  public static byte[] getBytesFor20H(byte offset, byte cnt) {
    byte[] res = new byte[5];
    res[0] = 0x20;
    res[1] = 0x05;
    res[2] = offset;
    res[3] = cnt;
    res[4] = getCheckSum(res, true);
    return res;
  }

/*    public static byte getLenOBDSensorAnswer(CommandsOffset offs) {
        byte res = 1;
        switch (offs) {
            case RPM:
            case Inj:
            case RPM2:
                res = 2;
                break;
            case IDECU:
                res = 10;
                break;
            case Errors1:
            case Errors2:
                res = 0x10;
                break;
        }
        return res;
    }

    public static Response buildAnswer(Command command, byte[] rawData) {
        Response answer = new Response();
        answer.setType(command.getType().answerType);
        answer.setCommand(command);
        int commandLen = command.getBytes().length;

        byte[] commandResponse = new byte[rawData.length - commandLen - 3];
        System.arraycopy(rawData, commandLen + 2, commandResponse, 0, commandResponse.length);

        answer.setCommandResponse(commandResponse);
        answer.setRawBytes(rawData);

        byte answerLen = getLenOBDSensorAnswer(command.getType());
        switch (answerLen) {
            case 1:
                answer.setValue(commandResponse[0]);
            case 2:
                answer.setValue(new BigInteger(new byte[] {commandResponse[0], commandResponse[1]}).intValue());
                break;
            default:
                answer.setDataArray(commandResponse);
        }
        return answer;
    } */
}
