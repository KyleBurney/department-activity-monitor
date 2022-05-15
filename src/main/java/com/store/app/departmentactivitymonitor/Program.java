package com.store.app.departmentactivitymonitor;

import com.store.app.departmentactivitymonitor.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Program implements CommandLineRunner {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private AppServicePool service;

    public static void main(String[] args) {
        SpringApplication.run(Program.class, args);
    }

    @Override
    public void run(String... args) {
        Runtime.getRuntime().addShutdownHook(new Thread(service::close));

        if (!appConfig.getEnvironment().equals("test")) {
            service.run();
        }
    }
}
