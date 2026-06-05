package com.core.port.messageBus.query;

public interface QueryDispatcher<T extends QueryInterface,R extends QueryResult> {
    public R dispatchQuery(T query);
}
