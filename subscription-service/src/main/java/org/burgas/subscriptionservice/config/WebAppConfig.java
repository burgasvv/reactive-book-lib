package org.burgas.subscriptionservice.config;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebAppConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public WebProperties.Resources webResources() {
        return new WebProperties.Resources();
    }
}
