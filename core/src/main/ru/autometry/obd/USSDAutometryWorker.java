package ru.autometry.obd;

/**
 * Created by jeck on 13/08/14
 */
public class USSDAutometryWorker {


  public static void main(String args[]) throws Exception {
/*
    String configFile = args[0];
    String localStateFile = args[1];
    String modemPort = args[2];
    String shortcode = args[3];

    long sendDelay = Long.parseLong(args[3]);

    Configurator<Config> commandsConfigurator = new OBDConfigurator();
    Config config = commandsConfigurator.configure(new File(configFile));

    OBDProcessor obd = new OBDProcessor(config);
    obd.start(config.getProperties());
    for (Listener listener : config.getListeners()) {
      obd.addListener(listener);
    }
    obd.addListener(new RetryListener());
    obd.getCommandQueue().addAll(config.getCommands());

    final StateSerializer<String> serializer = new JsonStringStateSerializer();
    final FileStateDumper memoryCurrentState = new FileStateDumper(serializer, new File(localStateFile));
    //final HttpJsonStateDumper httpDumper = new HttpJsonStateDumper(serializer, "http://smart54.ru/chukanov/index.jsp");

    final GSMModem gsmModem = new GSMModem(modemPort);
    final USSDDumper ussdDumper = new USSDDumper(gsmModem, shortcode);
    final OBDMomentum momentum = new OBDMomentum();
    final OBDState state = memoryCurrentState.load();

    state.setSessionId(state.getSessionId()+1);

    obd.addListener(new ShutdownProcessor() {
        @Override
        protected void processShutdown() {
            try {
                memoryCurrentState.dump(state);
                ussdDumper.dump(state);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void processUp() {

        }
    });

    obd.setMomentum(momentum);
    obd.setState(state);
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    service.scheduleWithFixedDelay(() -> {
      try {
        state.setLocation(gsmModem.location());
        memoryCurrentState.dump(state);
        ussdDumper.dump(state);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }, 1000l, sendDelay, TimeUnit.MILLISECONDS);

    obd.process();
    try {
      Thread.sleep((long) 10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } */
  }

  public void start() {

  }
}
