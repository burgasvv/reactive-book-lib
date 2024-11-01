package org.burgas.subscriptionservice.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebAppConfig {

    @Bean
    public ConnectionFactoryInitializer connectionFactoryInitializer(ConnectionFactory connectionFactory) {

        CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
        populator.addPopulators(
                new ResourceDatabasePopulator(new ClassPathResource("db/cleanupSubscriptionService.sql")),
                new ResourceDatabasePopulator(new ClassPathResource("db/initSubscriptionService.sql"))
        );

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(populator);
        return initializer;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
