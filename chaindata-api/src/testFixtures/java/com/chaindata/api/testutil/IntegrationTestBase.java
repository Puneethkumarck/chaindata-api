package com.chaindata.api.testutil;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(PostgresContainerExtension.class)
public abstract class IntegrationTestBase {}
