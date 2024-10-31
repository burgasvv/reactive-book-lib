package org.burgas.identityservice.interceptor;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class AuthenticationWebInterceptor implements ClientHttpRequestInterceptor {

    private final String authHeader;

    @Override
    public @NotNull ClientHttpResponse intercept(
            @NotNull HttpRequest request, byte @NotNull [] body, @NotNull ClientHttpRequestExecution execution

    ) throws IOException {
        request.getHeaders().set(AUTHORIZATION, authHeader);
        return execution.execute(request, body);
    }
}
