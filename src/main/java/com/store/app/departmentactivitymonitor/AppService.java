package com.store.app.departmentactivitymonitor;

public interface AppService extends Runnable, AutoCloseable {

    default String serviceName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Creates an AppService implementation from the provided functional interfaces
     */
    public static AppService create(String serviceName, Runnable runnable, AutoCloseable autoCloseable) {
        return new AppService() {
            @Override
            public void run() {
                runnable.run();
            }

            @Override
            public void close() throws Exception {
                autoCloseable.close();
            }

            @Override
            public String serviceName() {
                return serviceName;
            }
        };
    }
}
