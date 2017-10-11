package ru.autometry.obd.commands.listener;

import ru.autometry.obd.commands.Command;
import ru.autometry.obd.commands.Response;
import ru.autometry.obd.processing.CommandDispatcher;
import ru.autometry.utils.common.ByteUtils;
import ru.autometry.utils.common.InitUtils;
import ru.autometry.utils.common.Initable;

import java.util.Properties;

/**
 * Created by jeck on 20/08/14
 */
public class ECULogListener implements Listener, Initable {
  boolean dumpRequest;
  boolean dumpResponse;
  boolean dumpError;

  @Override
  public void onRequest(Command command, CommandDispatcher dispatcher) {
    if (dumpRequest)
      System.out.println("{\"type\":\"req\",\"cid\":\"" + command.getId() + "\",\"ts\":\"" + System.currentTimeMillis() + "\",\"dump\":\"" + ByteUtils.getHexString(command.getBytes()) + "\"}");
  }

  @Override
  public void onResponse(Response response, CommandDispatcher dispatcher) {
    if (dumpResponse)
      System.out.println("{\"type\":\"res\",\"cid\":\"" + response.getCommand().getId() + "\",\"ts\":\"" + response.getTime().getTime() + "\",\"dump\":\"" + ByteUtils.getHexString(response.getBytes()) + "\"}");
  }

  @Override
  public void onError(Exception e, Command command, CommandDispatcher dispatcher) {
    if (dumpError)
      System.out.println("{\"type\":\"err\",\"cid\":\"" + command.getId() + "\",\"ts\":\"" + System.currentTimeMillis() + "\",\"dump\":\"" + e.getLocalizedMessage() + "\"}");
  }

  @Override
  public void init(Properties config) throws Exception {
    dumpRequest = InitUtils.getBoolean("dump-req", false, config);
    dumpResponse = InitUtils.getBoolean("dump-res", true, config);
    dumpError = InitUtils.getBoolean("dump-err", true, config);
  }
}
