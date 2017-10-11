package ru.autometry.utils.processing;

import ru.autometry.utils.common.InitUtils;
import ru.autometry.utils.common.Initable;

import java.util.Properties;

public class FastSpeedShaper implements SpeedShaper, Initable {
  /**
   * Number of requests per second
   */
  int speed;
  long delay;
  long prevTime;
  long overDelay;

  public FastSpeedShaper() {
  }

  public FastSpeedShaper(int speed) {
    init(speed);
  }

  public void init(int speed) {
    this.speed = speed;
    delay = 1000000000L / speed;
    prevTime = System.nanoTime();
  }

  public int getSpeed() {
    return speed;
  }

  /**
   * Method waits for interval when next send attempt allowed
   */
  public synchronized void request() throws InterruptedException {
    long opTime = System.nanoTime() - prevTime;
    if (opTime >= delay) {
      overDelay = 0;
    } else if (delay > opTime + overDelay) {
      long toSleep = delay - opTime - overDelay;
      long delayStart = System.nanoTime();
      Thread.sleep(toSleep / 1000000, (int) (toSleep % 1000000));
      long delayTime = System.nanoTime() - delayStart;
      overDelay = delayTime - toSleep;
    } else {
      overDelay -= delay - opTime;
      if (overDelay < -delay) {
        overDelay = -delay;
      }
    }
    prevTime = System.nanoTime();
  }

  @Override
  public void init(Properties config) throws Exception {
    this.init(InitUtils.getInt("speed", 10, config));
  }
}
