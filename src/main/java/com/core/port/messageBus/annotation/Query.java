package com.core.port.messageBus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.core.port.messageBus.query.QueryResult;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Query {
    Class<? extends QueryResult> returnType();
}