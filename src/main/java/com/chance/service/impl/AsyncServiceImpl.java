package com.chance.service.impl;

import com.chance.service.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

/**
 * <p>
 *
 * <p>
 *
 * @author chance
 * @since 2020-09-09
 */
@Slf4j
@Service
public class AsyncServiceImpl implements AsyncService {

    @Autowired
    private TaskExecutor asyncServiceExecutor;

    @Override
    @Async
    public void executeAsync() {
        log.info("start executeAsync");
        asyncServiceExecutor.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });

        log.info("end executeAsync");
    }

    @Override
    @Async("asyncServiceExecutor")
    public void executeAsync2() {
        log.info("start executeAsync");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        log.info("end executeAsync");
    }
}
