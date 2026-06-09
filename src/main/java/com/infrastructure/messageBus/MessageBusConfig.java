package com.infrastructure.messageBus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;


@Configuration
public class MessageBusConfig {
    @Value("${messagebus.virtual}")
    boolean virtual;

    @Autowired
    private ApplicationContext ctx;

    private ThreadPoolTaskExecutor commandExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(5);
        executor.setThreadNamePrefix("command-bus-");
        return executor;
    }

    private ThreadPoolTaskExecutor queryExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(5);
        executor.setThreadNamePrefix("query-bus-");
        return executor;
    }

    private SimpleAsyncTaskExecutor virtualExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setVirtualThreads(true);
        executor.setConcurrencyLimit(20);
        executor.setThreadNamePrefix("virtual-bus-");
        return executor;
    }

    @Bean(name = "commandDispatcher")
    public TaskExecutorCommandDispatcher commandDispatcher() {
        return new TaskExecutorCommandDispatcher(virtual ? virtualExecutor() : commandExecutor(), ctx);
    }

    @Bean(name = "queryDispatcher")
    public TaskExecutorQueryDispatcher queryDispatcher() {
        return new TaskExecutorQueryDispatcher(virtual ? virtualExecutor() : queryExecutor(), ctx);
    }
}
