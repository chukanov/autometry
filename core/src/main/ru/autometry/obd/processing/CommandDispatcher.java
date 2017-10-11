package ru.autometry.obd.processing;

import ru.autometry.obd.commands.Command;

import java.util.Queue;

/**
 * Created by jeck on 13/08/14
 */
public interface CommandDispatcher {
  public Queue<Command> getCommandQueue();
}
