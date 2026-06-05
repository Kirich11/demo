package com.core.port.messageBus.query;

public interface QueryHandlerInterface<T extends QueryInterface, R extends QueryResult> {
    public R handle(T query);
}
