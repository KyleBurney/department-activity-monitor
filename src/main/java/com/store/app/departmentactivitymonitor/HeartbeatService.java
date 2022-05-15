package com.store.app.departmentactivitymonitor;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.RateLimiter;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HeartbeatService implements AppService {

    private final Logger log;
    private final RateLimiter rateLimiter;
    private final AtomicBoolean gate = new AtomicBoolean();

    @Autowired
    public HeartbeatService(Logger logger) {
        this(logger, 0.1);
    }

    @VisibleForTesting
    public HeartbeatService(Logger logger, double permitsPerSecond) {
        this.log = logger;
        this.rateLimiter = RateLimiter.create(permitsPerSecond);
    }

    @Override
    public void run() {
        gate.set(true);
        while (gate.get()) {
            rateLimiter.acquire();
        }
    }

    @Override
    public void close() {
        if (gate.compareAndSet(true, false)) {
            log.info("heartbeat closed");
        }
    }
}
