package org.burgas.identityservice.service;

import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.dto.IdentityRequest;
import org.burgas.identityservice.dto.IdentityResponse;
import org.burgas.identityservice.exception.IdentityWrongIssuesException;
import org.burgas.identityservice.handler.WebClientHandler;
import org.burgas.identityservice.mapper.IdentityMapper;
import org.burgas.identityservice.repository.IdentityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

import static org.springframework.transaction.annotation.Isolation.REPEATABLE_READ;
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
                .flatMap(identity -> identityMapper.toIdentityResponse(Mono.just(identity)))
                .log("IDENTITY-SERVICE FindAllIdentities");
    }

    public Mono<IdentityResponse> findByUsername(String username) {
        return identityRepository.findIdentityByUsername(username)
                .flatMap(identity -> identityMapper.toIdentityResponse(Mono.just(identity)));
    }

    public Mono<IdentityResponse> findById(String identityId) {
        return identityRepository.findById(Long.valueOf(identityId))
                .flatMap(identity -> identityMapper.toIdentityResponse(Mono.just(identity)));
    }

    @Transactional(
            isolation = REPEATABLE_READ,
            propagation = REQUIRED,
            rollbackFor = Exception.class
    )
    public Mono<IdentityResponse> create(Mono<IdentityRequest> identityRequestMono, String authValue) {
        return webClientHandler.getPrincipal(authValue)
                .flatMap(
                        identityPrincipal -> Optional.of(identityPrincipal)
                                .filter(principal -> !principal.getIsAuthenticated())
                                .map(
                                        ip -> identityMapper.toIdentityCreate(identityRequestMono)
                                                .flatMap(identityRepository::save)
                                                .flatMap(identity -> identityMapper.toIdentityResponse(Mono.just(identity)))
                                )
                                .orElseGet(
                                        () -> Mono.error(
                                                new RuntimeException("Выйдите из аккаунта, чтобы создать новый")
                                        )
                                )
                );
    }

    @Transactional(
            isolation = REPEATABLE_READ,
            propagation = REQUIRED,
            rollbackFor = Exception.class
    )
    public Mono<IdentityResponse> update(Mono<IdentityRequest> identityRequestMono, String authValue) {
        return identityRequestMono.flatMap(
                identityRequest -> webClientHandler.getPrincipal(authValue)
                        .flatMap(
                                identityPrincipal -> {
                                    if (identityPrincipal.getIsAuthenticated() &&
                                        Objects.equals(identityPrincipal.getId(), identityRequest.getId())
                                    ) {
                                        return identityMapper.toIdentityUpdate(Mono.just(identityRequest))
                                                .flatMap(identityRepository::save)
                                                .flatMap(identity -> identityMapper.toIdentityResponse(Mono.just(identity)));
                                    } else
                                        return Mono.error(
                                                new IdentityWrongIssuesException("Пользователь не авторизован и не имеет прав доступа")
                                        );
                                }
                        )
        );
    }

    @Transactional(
            isolation = REPEATABLE_READ,
            propagation = REQUIRED,
            rollbackFor = Exception.class
    )
    public Mono<String> delete(String identityId, String authValue) {
        return webClientHandler.getPrincipal(authValue)
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
                                        new IdentityWrongIssuesException("Пользователь не авторизован и не имеет прав доступа")
                                );
                        }
                );
    }
}
