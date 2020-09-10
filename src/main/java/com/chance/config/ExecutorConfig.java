package com.chance.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * 配置线程池
 * <p>
 *
 * @author chance
 * @since 2020-09-09
 */
@Configuration
@EnableAsync
public class ExecutorConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorConfig.class);

    @Bean
    public TaskExecutor asyncServiceExecutor() {
        LOGGER.info("start asyncServiceExecutor");

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 配置核心线程数
        executor.setCorePoolSize(10);
        // 最大线程数
        executor.setMaxPoolSize(20);
        // 队列大小
        executor.setQueueCapacity(200);
        // 线程池维护线程所允许的空闲时间，默认为60秒
        executor.setKeepAliveSeconds(60);
        // 线程池中的名称前缀
        executor.setThreadNamePrefix("async-service-");

        // rejection-policy：当pool已经达到max size的时候，如何处理新任务

        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        //执行初始化
        executor.initialize();

        return executor;
    }
}
