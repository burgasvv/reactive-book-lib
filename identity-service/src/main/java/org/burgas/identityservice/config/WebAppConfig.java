package org.burgas.identityservice.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebAppConfig {

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

        CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
        populator.setPopulators(
                new ResourceDatabasePopulator(new ClassPathResource("db/cleanupIdentityService.sql")),
                new ResourceDatabasePopulator(new ClassPathResource("db/initIdentityService.sql"))
        );

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(populator);

        return initializer;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebProperties.Resources webProperties() {
        return new WebProperties.Resources();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

}
