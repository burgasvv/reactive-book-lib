package org.burgas.bookservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Configuration
public class WebAppConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .filter(basicAuthentication("admin", "admin"))
                .build();
    }
}
