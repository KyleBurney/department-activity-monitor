package com.store.app.departmentactivitymonitor;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AppServicePool implements Runnable, AutoCloseable {

    private final Logger log;
    private final List<AppService> services;
    private final ExecutorService executor;

    @Autowired
    public AppServicePool(Logger log, List<AppService> services) {
        this.log = log;
        this.services = services;
        this.executor = Executors.newFixedThreadPool(services.size());
    }

    @Override
    public void run() {
        log.info(String.format("starting %s", getClass().getSimpleName()));
        for (AppService service : services) {
            log.info(String.format("running %s", service.serviceName()));
            executor.execute(service);
        }
    }

    @Override
    public void close() {
        for (AppService service : services) {
            try {
                log.info(String.format("closing %s", service.serviceName()));
                service.close();
            } catch (Exception e) {
                log.error(String.format("error closing %s", service.serviceName()));
            }
        }

        try {
            log.info(String.format("stopping %s", getClass().getSimpleName()));
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(String.format("error stopping %s", getClass().getSimpleName()));
        }
    }
}
