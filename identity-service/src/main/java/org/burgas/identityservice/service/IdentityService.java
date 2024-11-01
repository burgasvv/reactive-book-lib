package org.burgas.identityservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.dto.IdentityRequest;
import org.burgas.identityservice.dto.IdentityResponse;
import org.burgas.identityservice.entity.Identity;
import org.burgas.identityservice.exception.IdentityWrongIssuesException;
import org.burgas.identityservice.handler.WebClientHandler;
import org.burgas.identityservice.mapper.IdentityMapper;
import org.burgas.identityservice.repository.IdentityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IdentityService {

    private final IdentityRepository identityRepository;
    private final IdentityMapper identityMapper;
    private final WebClientHandler webClientHandler;

    public Flux<IdentityResponse> findAll() {
        return identityRepository.findAll()
                .map(identityMapper::toIdentityResponse);
    }

    public Mono<IdentityResponse> findByUsername(String username) {
        return identityRepository.findIdentityByUsername(username)
                .map(identityMapper::toIdentityResponse);
    }

    public Mono<IdentityResponse> findById(String identityId) {
        return identityRepository.findById(Long.valueOf(identityId))
                .map(identityMapper::toIdentityResponse);
    }

    @Transactional(
            isolation = SERIALIZABLE,
            propagation = REQUIRED,
            rollbackFor = {
                    Exception.class
            }
    )
    public Mono<String> create(Mono<IdentityRequest> identityRequestMono) {
        return identityRequestMono.flatMap(
                identityRequest -> {
                    Identity create = identityMapper.toIdentityCreate(identityRequest);
                    return identityRepository.save(create)
                            .map(identity -> "Пользователь с именем " + identity.getUsername() + " успешно создан");
                }
        );
    }

    @Transactional(
            isolation = SERIALIZABLE,
            propagation = REQUIRED,
            rollbackFor = {
                    Exception.class
            }
    )
    public Mono<IdentityResponse> update(Mono<IdentityRequest> identityRequestMono, String authorization) {
        return identityRequestMono.flatMap(
                identityRequest -> webClientHandler.getPrincipal(authorization)
                        .flatMap(
                                identityPrincipal -> {

                                    if (identityPrincipal.getIsAuthenticated() &&
                                        Objects.equals(identityPrincipal.getId(), identityRequest.getId())
                                    ) {

                                        Identity update = identityMapper.toIdentityUpdate(identityRequest);
                                        return identityRepository.save(update)
                                                .map(identityMapper::toIdentityResponse);

                                    } else
                                        return Mono.error(
                                                new IdentityWrongIssuesException("Пользователь не авторизован или жулик")
                                        );
                                }
                        )
        );
    }

    @Transactional(
            isolation = SERIALIZABLE,
            propagation = REQUIRED,
            rollbackFor = Exception.class
    )
    public Mono<String> delete(String identityId, String authorizationValue) {
        return webClientHandler.getPrincipal(authorizationValue)
                .flatMap(
                        identityPrincipal -> {

                            if (identityPrincipal.getIsAuthenticated() &&
                                Objects.equals(identityPrincipal.getId(), Long.valueOf(identityId))
                            ) {
                                return identityRepository.deleteById(Long.valueOf(identityId))
                                        .then(
                                                Mono.fromCallable(() -> "Пользователь с идентификатором "
                                                                        + identityId + " успешно удален")
                                        );
                            } else
                                return Mono.error(
                                        new IdentityWrongIssuesException("Пользователь не авторизован или жулик")
                                );
                        }
                );
    }
}
