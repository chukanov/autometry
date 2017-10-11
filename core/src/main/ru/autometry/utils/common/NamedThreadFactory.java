package ru.autometry.utils.common;

/**
 * Created by jeck on 31/07/16.
 */
import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {

  private int counter;
  private String threadNamePrefix;

  public NamedThreadFactory(String prefix) {
    this(prefix, 0);
  }

  public NamedThreadFactory(String prefix, int initialCounter) {
    this.threadNamePrefix = prefix;
    this.counter = initialCounter;
  }

  public final Thread newThread(Runnable r) {
    return new Thread(r, threadNamePrefix + (counter++));
  }
}
