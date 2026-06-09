package com.infrastructure.messageBus;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import com.core.port.messageBus.annotation.Command;
import com.core.port.messageBus.annotation.Query;
import com.core.port.messageBus.command.CommandHandlerInterface;
import com.core.port.messageBus.query.QueryHandlerInterface;

@Component
public class MessageBusHandlerValidator implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof CommandHandlerInterface || bean instanceof QueryHandlerInterface)) return bean;

        if (bean instanceof CommandHandlerInterface) {
            return validateCommandHandler(bean, beanName);
        }

        if (bean instanceof QueryHandlerInterface) {
            return validateQueryHandler(bean, beanName);
        }

        throw new IllegalStateException("Unkown class "+ beanName);
    }

    private Object validateCommandHandler(Object bean, String beanName) {
        ResolvableType handlerType = ResolvableType
                .forClass(AopUtils.getTargetClass(bean))
                .as(CommandHandlerInterface.class);

        Class<?> dispatchableType = handlerType.getGeneric(0).resolve();
        Class<?> declaredResult = handlerType.getGeneric(1).resolve();

        if (dispatchableType == null || declaredResult == null) {
            throw new IllegalStateException(beanName + " must define Dispatchable and DispatchableResult generic args");
        }

        Command annotation = dispatchableType.getAnnotation(Command.class);
        if (annotation == null) {
            throw new IllegalStateException(dispatchableType.getSimpleName() + " is missing @Command annotation");
        }

        if (!annotation.returnType().equals(declaredResult)) {
        throw new IllegalStateException(
                "Return type mismatch on " + beanName + ": " +
                "Command.returnType()=" + annotation.returnType().getSimpleName() +
                " but handler declares R=" + declaredResult.getSimpleName());
        }

        return bean;
    }

    private Object validateQueryHandler(Object bean, String beanName) {
        ResolvableType handlerType = ResolvableType
                .forClass(AopUtils.getTargetClass(bean))
                .as(QueryHandlerInterface.class);

        Class<?> dispatchableType = handlerType.getGeneric(0).resolve();
        Class<?> declaredResult = handlerType.getGeneric(1).resolve();

        Query annotation = dispatchableType.getAnnotation(Query.class);
        if (annotation == null) {
            throw new IllegalStateException(dispatchableType.getSimpleName() + " is missing @Query annotation");
        }

        if (!annotation.returnType().equals(declaredResult)) {
        throw new IllegalStateException(
                "Return type mismatch on " + beanName + ": " +
                "Query.returnType()=" + annotation.returnType().getSimpleName() +
                " but handler declares R=" + declaredResult.getSimpleName());
        }

        return bean;
    }

}
