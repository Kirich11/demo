package com.infrastructure.messageBus;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.task.TaskExecutor;

import com.core.port.messageBus.annotation.Command;
import com.core.port.messageBus.command.CommandDispatcher;
import com.core.port.messageBus.command.CommandHandlerInterface;
import com.core.port.messageBus.command.CommandInterface;
import com.core.port.messageBus.command.CommandResult;

public class ThreadPoolCommandDispatcher implements CommandDispatcher {

    private final TaskExecutor commandExecutor;
    private final ApplicationContext ctx;

    public ThreadPoolCommandDispatcher(TaskExecutor commandExecutor, ApplicationContext ctx) {
        this.commandExecutor = commandExecutor;
        this.ctx = ctx;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CommandInterface, R extends CommandResult> R dispatchCommand(T command) {
        Class<? extends CommandInterface> commandClass = command.getClass();
        // Safe: CommandHandlerValidator asserts at startup that returnType() == handler's R
        Class<R> resultClass = (Class<R>) commandClass.getAnnotation(Command.class).returnType();
        CommandHandlerInterface<T, R> handler = resolveHandler(commandClass, resultClass);
        CompletableFuture<R> result = CompletableFuture.supplyAsync(() -> handler.handle(command), commandExecutor);

        try {
            return result.get();
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Command execution failed: " + commandClass.getSimpleName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends CommandInterface, R extends CommandResult> CommandHandlerInterface<T, R> resolveHandler(
            Class<? extends CommandInterface> commandClass, Class<R> resultClass) {
        String[] names = ctx.getBeanNamesForType(
                ResolvableType.forClassWithGenerics(CommandHandlerInterface.class, commandClass, resultClass));
        // Safe: generic params are erased at runtime; structural match guaranteed by CommandHandlerValidator
        return (CommandHandlerInterface<T, R>) ctx.getBean(names[0]);
    }
}
