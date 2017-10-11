package ru.autometry.obd;

/**
 * Created by jeck on 22/08/14
 */
public class TestFileProcessor {
  public static void main(String[] args) throws Exception {
/*    Configurator<Config> commandsConfigurator = new OBDConfigurator();
    Config config = commandsConfigurator.configure(new File(args[0]));
    final StateSerializer<String> serializer = new JsonStringStateSerializer();
    final FileStateDumper dumper = new FileStateDumper(serializer, new File("work/state"));
    //final HttpJsonStateDumper httpDumper = new HttpJsonStateDumper(serializer, "http://smart54.ru/chukanov/index.jsp");
    final GSMModem sender = new GSMModem(args[2]);
    final USSDDumper ussdDumper = new USSDDumper(sender, "312*765*s54");
    final OBDMomentum momentum = new OBDMomentum();
    final OBDState state = dumper.load();
    state.setSessionId(state.getSessionId()+1);
    final StateSerializer<String> byteSerializer = new Base64Serializer();

    CommandProcessor obd = new FileProcessor(new File(args[1]), config);
    for (Listener listener : config.getListeners()) {
      obd.addListener(listener);
    }
    //obd.addListener(new RetryListener());
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    service.scheduleWithFixedDelay(new Runnable() {
      @Override
      public void run() {
        try {
          state.setLocation(sender.location());
          String base64 = byteSerializer.serialize(state);
          System.out.println(base64);
          OBDState s = byteSerializer.load(base64);
          System.out.println(serializer.serialize(s));
          dumper.dump(state);
          ussdDumper.dump(state);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }, 1000l, 5000l, TimeUnit.MILLISECONDS);
    obd.process();
*/
  }
}
