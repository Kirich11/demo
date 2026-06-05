package com.core.port.messageBus.command;

public interface CommandDispatcher {
    <T extends CommandInterface, R extends CommandResult> R dispatchCommand(T command);
}
