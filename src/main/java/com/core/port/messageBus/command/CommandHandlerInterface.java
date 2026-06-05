package com.core.port.messageBus.command;

public interface CommandHandlerInterface<T extends CommandInterface, R extends CommandResult> {
    public R handle(T command);
}
