package com.chaindata.api;

import com.chaindata.api.testutil.IntegrationTestBase;
import com.chaindata.api.testutil.KafkaContainerExtension;
import com.chaindata.api.testutil.RedisContainerExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(RedisContainerExtension.class)
@ExtendWith(KafkaContainerExtension.class)
class ChainDataApiApplicationTest extends IntegrationTestBase {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void shouldLoadApplicationContext() {
        assertThat(applicationContext).isNotNull();
    }
}
