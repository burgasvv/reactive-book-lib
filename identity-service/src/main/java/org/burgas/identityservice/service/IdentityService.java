package org.burgas.identityservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.dto.IdentityPrincipal;
import org.burgas.identityservice.dto.IdentityRequest;
import org.burgas.identityservice.dto.IdentityResponse;
import org.burgas.identityservice.exception.IdentityNotAuthenticated;
import org.burgas.identityservice.exception.IdentityNotMatchException;
import org.burgas.identityservice.handler.RestTemplateHandler;
import org.burgas.identityservice.mapper.IdentityMapper;
import org.burgas.identityservice.repository.IdentityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IdentityService {

    private final IdentityRepository identityRepository;
    private final IdentityMapper identityMapper;
    private final RestTemplateHandler restTemplateHandler;

    public Flux<IdentityResponse> findAll() {
        return identityRepository.findAll()
                .map(identityMapper::toIdentityResponse);
    }

    public Mono<IdentityResponse> findByUsername(String username) {
        return identityRepository.findIdentityByUsername(username)
                .map(identityMapper::toIdentityResponse);
    }

    @Transactional(
            isolation = SERIALIZABLE,
            propagation = REQUIRED,
            rollbackFor = {
                    RuntimeException.class,
                    InterruptedException.class,
                    ExecutionException.class
            }
    )
    public Mono<IdentityResponse> create(Mono<IdentityRequest> identityRequestMono) {
        try {
            //noinspection BlockingMethodInNonBlockingContext
            return Mono.just(
                    identityMapper.toIdentityResponse(
                            identityRepository.save(
                                    identityMapper.toIdentityCreate(identityRequestMono.toFuture().get())
                            )
                                    .toFuture().get()
                    )
            );

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(
            isolation = SERIALIZABLE,
            propagation = REQUIRED,
            rollbackFor = {
                    RuntimeException.class,
                    InterruptedException.class,
                    ExecutionException.class
            }
    )
    public Mono<IdentityResponse> update(Mono<IdentityRequest> identityRequestMono, String authorization) {

        try {
            //noinspection BlockingMethodInNonBlockingContext
            IdentityPrincipal principal = restTemplateHandler.getPrincipal(authorization).toFuture().get();

            if (principal.getIsAuthenticated()) {
                //noinspection BlockingMethodInNonBlockingContext
                IdentityRequest identityRequest = identityRequestMono.toFuture().get();
                if (
                        Objects.equals(identityRequest.getId(), principal.getId())
                ) {
                    //noinspection BlockingMethodInNonBlockingContext
                    return Mono.just(
                            identityMapper.toIdentityResponse(
                                    identityRepository.save(
                                                    identityMapper.toIdentityUpdate(identityRequest)
                                            )
                                            .toFuture().get()
                            )
                    );

                } else
                    throw new IdentityNotMatchException("Попытка изменения данных пользователя с чужого аккаунта");

            } else
                throw new IdentityNotAuthenticated("Пользователь не авторизован");

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(
            isolation = SERIALIZABLE,
            propagation = REQUIRED,
            rollbackFor = RuntimeException.class
    )
    public Mono<String> delete(String identityId) {
        return identityRepository.deleteById(Long.valueOf(identityId))
                .then(
                        Mono.fromCallable(() -> "Пользователь с идентификатором " + identityId + " удален")
                );
    }
}
