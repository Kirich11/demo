package com.infrastructure.messageBus;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import com.core.port.messageBus.annotation.Command;
import com.core.port.messageBus.command.CommandHandlerInterface;

@Component
public class CommandHandlerValidator implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof CommandHandlerInterface)) return bean;

        ResolvableType handlerType = ResolvableType
                .forClass(AopUtils.getTargetClass(bean))
                .as(CommandHandlerInterface.class);

        Class<?> commandType = handlerType.getGeneric(0).resolve();
        Class<?> declaredResult = handlerType.getGeneric(1).resolve();

        if (commandType == null || declaredResult == null) {
            throw new IllegalStateException(beanName + " must define Command and CommandResult generic args");
        }

        Command annotation = commandType.getAnnotation(Command.class);
        if (annotation == null) {
            throw new IllegalStateException(commandType.getSimpleName() + " is missing @Command annotation");
        }

        if (!annotation.returnType().equals(declaredResult)) {
            throw new IllegalStateException(
                    "Return type mismatch on " + beanName + ": " +
                    "@Command.returnType()=" + annotation.returnType().getSimpleName() +
                    " but handler declares R=" + declaredResult.getSimpleName());
        }

        return bean;
    }
}
