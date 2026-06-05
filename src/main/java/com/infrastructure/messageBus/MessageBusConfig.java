package com.infrastructure.messageBus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;


@Configuration
public class MessageBusConfig {
    @Autowired
    private ApplicationContext ctx;

    @Bean(name = "commandExecutor")
    public ThreadPoolTaskExecutor commandExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("command-bus-");
        return executor;
    }

    @Bean(name = "queryExecutor")
    public ThreadPoolTaskExecutor cpuTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("query-bus-");
        return executor;
    }

    @Bean(name = "commandDispatcher")
    public ThreadPoolCommandDispatcher commandDispatcher() {
        return new ThreadPoolCommandDispatcher(commandExecutor(), ctx);
    }
}
