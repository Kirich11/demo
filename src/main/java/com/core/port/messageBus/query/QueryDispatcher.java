package com.core.port.messageBus.query;

public interface QueryDispatcher {
    public <T extends QueryInterface,R extends QueryResult> R dispatchQuery(T query);
}
