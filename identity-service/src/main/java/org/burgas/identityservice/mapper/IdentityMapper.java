package org.burgas.identityservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.dto.IdentityRequest;
import org.burgas.identityservice.dto.IdentityResponse;
import org.burgas.identityservice.entity.Identity;
import org.burgas.identityservice.repository.IdentityRepository;
import org.burgas.identityservice.service.AuthorityService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class IdentityMapper {

    private final AuthorityService authorityService;
    private final IdentityRepository identityRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<Identity> toIdentityCreate(Mono<IdentityRequest> identityRequestMono) {
        return identityRequestMono
                .map(
                        identityRequest -> Identity.builder()
                                .id(identityRequest.getId() == null ? 0L : identityRequest.getId())
                                .username(identityRequest.getUsername())
                                .password(passwordEncoder.encode(identityRequest.getPassword()))
                                .email(identityRequest.getEmail())
                                .authorityId(1L)
                                .enabled(true)
                                .isNew(true)
                                .build()
                );
    }

    public Mono<Identity> toIdentityUpdate(Mono<IdentityRequest> identityRequestMono) {
        return identityRequestMono
                .publishOn(Schedulers.boundedElastic())
                .handle(
                        (identityRequest, identitySynchronousSink) ->
                        {
                            try {
                                Long tempId = identityRequest.getId() == null ? 0L : identityRequest.getId();
                                identitySynchronousSink.next(
                                        Identity.builder()
                                                .id(tempId)
                                                .username(identityRequest.getUsername())
                                                .password(identityRepository.findById(tempId).toFuture().get().getPassword())
                                                .email(identityRequest.getEmail())
                                                .authorityId(1L)
                                                .enabled(true)
                                                .isNew(false)
                                                .build()
                                );
                            } catch (InterruptedException | ExecutionException e) {
                                identitySynchronousSink.error(new RuntimeException(e));
                            }
                        }
                );
    }

    public Mono<IdentityResponse> toIdentityResponse(Mono<Identity> identityMono) {
        return identityMono
                .publishOn(Schedulers.boundedElastic())
                .handle(
                        (identity, identityResponseSynchronousSink) ->
                        {
                            try {
                                identityResponseSynchronousSink.next(
                                        IdentityResponse.builder()
                                                .id(identity.getId())
                                                .username(identity.getUsername())
                                                .password(identity.getPassword())
                                                .email(identity.getEmail())
                                                .enabled(identity.getEnabled())
                                                .authorityResponse(
                                                        authorityService.findById(
                                                                        String.valueOf(identity.getAuthorityId())
                                                                )
                                                                .toFuture().get()
                                                )
                                                .build()
                                );
                            } catch (InterruptedException | ExecutionException e) {
                                identityResponseSynchronousSink.error(new RuntimeException(e));
                            }
                        }
                )
                .log("IDENTITY-MAPPER toIdentityResponse")
                .cast(IdentityResponse.class);
    }
}
