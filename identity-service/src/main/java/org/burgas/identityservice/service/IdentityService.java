package org.burgas.identityservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.dto.IdentityRequest;
import org.burgas.identityservice.dto.IdentityResponse;
import org.burgas.identityservice.mapper.IdentityMapper;
import org.burgas.identityservice.repository.IdentityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IdentityService {

    private final IdentityRepository identityRepository;
    private final IdentityMapper identityMapper;

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
            rollbackFor = RuntimeException.class
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
            rollbackFor = RuntimeException.class
    )
    public Mono<IdentityResponse> update(Mono<IdentityRequest> identityRequestMono) {
        try {
            //noinspection BlockingMethodInNonBlockingContext
            return Mono.just(
                    identityMapper.toIdentityResponse(
                            identityRepository.save(
                                            identityMapper.toIdentityUpdate(identityRequestMono.toFuture().get())
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
            rollbackFor = RuntimeException.class
    )
    public Mono<String> delete(String identityId) {
        return identityRepository.deleteById(Long.valueOf(identityId))
                .then(
                        Mono.fromCallable(() -> "Пользователь с идентификатором " + identityId + " удален")
                );
    }
}
