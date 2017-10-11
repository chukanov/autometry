package ru.autometry.utils.processing;

/**
 * This interface defines limited speed shapers
 */
public interface SpeedShaper {
  public void init(int speed);

  public void request() throws InterruptedException;
}
