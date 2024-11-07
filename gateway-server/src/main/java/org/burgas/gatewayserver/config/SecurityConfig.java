package org.burgas.gatewayserver.config;

import lombok.RequiredArgsConstructor;
import org.burgas.gatewayserver.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http

                .httpBasic(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .authorizeExchange(
                        exchange -> exchange

                                .pathMatchers(
                                        "/identities/create", "/auth/principal","/books/{book-id}",
                                        "/books","/books/by-author/{author-id}","/books/by-genre/{genre-id}",
                                        "/books/subscription/{subscription-id}", "/authors","/authors/{author-id}",
                                         "/genres","/genres/{genre-id}", "/payment-types", "/payment-types/{paymentType-id}"
                                )
                                .permitAll()

                                .pathMatchers(
                                        "/identities","/identities/{username}",
                                        "/identities/identity/{identity-id}",
                                        "/identities/edit","/identities/delete",
                                        "/authorities/**", "/subscriptions/**",
                                        "/books/create", "/books/edit", "/genres/create","/genres/edit",
                                        "/authors/create", "/authors/edit", "/payment-types/**", "/payments/**"
                                )
                                .hasAnyAuthority("USER", "ADMIN")
                )
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())

                .build();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager manager =
                new UserDetailsRepositoryReactiveAuthenticationManager(customUserDetailsService);
        manager.setPasswordEncoder(passwordEncoder());
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
