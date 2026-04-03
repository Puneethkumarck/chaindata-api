package com.chaindata.api.testutil;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresContainerExtension implements BeforeAllCallback {

    private static final DockerImageName TIMESCALEDB_IMAGE =
            DockerImageName.parse("timescale/timescaledb:latest-pg16").asCompatibleSubstituteFor("postgres");

    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> CONTAINER =
            new PostgreSQLContainer<>(TIMESCALEDB_IMAGE)
                    .withDatabaseName("chaindata_test")
                    .withUsername("test")
                    .withPassword("test");

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!CONTAINER.isRunning()) {
            CONTAINER.start();
        }
        var jdbcUrl = CONTAINER.getJdbcUrl();
        var r2dbcUrl = jdbcUrl.replace("jdbc:", "r2dbc:");

        System.setProperty("spring.r2dbc.url", r2dbcUrl);
        System.setProperty("spring.r2dbc.username", CONTAINER.getUsername());
        System.setProperty("spring.r2dbc.password", CONTAINER.getPassword());

        System.setProperty("spring.flyway.url", jdbcUrl);
        System.setProperty("spring.flyway.user", CONTAINER.getUsername());
        System.setProperty("spring.flyway.password", CONTAINER.getPassword());
    }
}
