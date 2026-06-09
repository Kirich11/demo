package com.infrastructure.messageBus;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.task.TaskExecutor;

import com.core.port.messageBus.query.QueryDispatcher;
import com.core.port.messageBus.query.QueryHandlerInterface;
import com.core.port.messageBus.query.QueryInterface;
import com.core.port.messageBus.query.QueryResult;
import com.core.port.messageBus.annotation.Query;

public class TaskExecutorQueryDispatcher implements QueryDispatcher {

    private final TaskExecutor queryExecutor;
    private final ApplicationContext ctx;

    public TaskExecutorQueryDispatcher(TaskExecutor queryExecutor, ApplicationContext ctx) {
        this.queryExecutor = queryExecutor;
        this.ctx = ctx;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends QueryInterface, R extends QueryResult> R dispatchQuery(T query) {
        Class<? extends QueryInterface> queryClass = query.getClass();
        // Safe: MessageBusHandlerValidation asserts at startup that returnType() == handler's R
        Class<R> resultClass = (Class<R>) queryClass.getAnnotation(Query.class).returnType();
        QueryHandlerInterface<T, R> handler = resolveHandler(queryClass, resultClass);
        CompletableFuture<R> result = CompletableFuture.supplyAsync(() -> handler.handle(query), queryExecutor);

        try {
            return result.get();
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Query execution failed: " + queryClass.getSimpleName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends QueryInterface, R extends QueryResult> QueryHandlerInterface<T, R> resolveHandler(
            Class<? extends QueryInterface> queryClass,
            Class<R> resultClass
    ) {
        String[] names = ctx.getBeanNamesForType(
                ResolvableType.forClassWithGenerics(QueryHandlerInterface.class, queryClass, resultClass)
            );
        // Safe: generic params are erased at runtime; structural match guaranteed by MessageBusHandlerValidation
        return (QueryHandlerInterface<T, R>) ctx.getBean(names[0]);
    }
}
