package com.chaindata.api.testutil;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;

public class RedisContainerExtension implements BeforeAllCallback {

    private static final int REDIS_PORT = 6379;

    @SuppressWarnings("resource")
    private static final GenericContainer<?> CONTAINER =
            new GenericContainer<>("redis/redis-stack-server:latest").withExposedPorts(REDIS_PORT);

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!CONTAINER.isRunning()) {
            CONTAINER.start();
        }
        System.setProperty("spring.data.redis.host", CONTAINER.getHost());
        System.setProperty("spring.data.redis.port", String.valueOf(CONTAINER.getMappedPort(REDIS_PORT)));
    }
}
