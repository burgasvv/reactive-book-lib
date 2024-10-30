package org.burgas.identityservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.dto.IdentityRequest;
import org.burgas.identityservice.dto.IdentityResponse;
import org.burgas.identityservice.entity.Identity;
import org.burgas.identityservice.repository.IdentityRepository;
import org.burgas.identityservice.service.AuthorityService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class IdentityMapper {

    private final AuthorityService authorityService;
    private final IdentityRepository identityRepository;
    private final PasswordEncoder passwordEncoder;

    public Identity toIdentityCreate(IdentityRequest identityRequest) {
        return Identity.builder()
                .id(identityRequest.getId())
                .username(identityRequest.getUsername())
                .password(passwordEncoder.encode(identityRequest.getPassword()))
                .email(identityRequest.getEmail())
                .authorityId(1L)
                .enabled(true)
                .isNew(true)
                .build();
    }

    public Identity toIdentityUpdate(IdentityRequest identityRequest) {
        try {
            return Identity.builder()
                    .id(identityRequest.getId())
                    .username(identityRequest.getUsername())
                    .password(
                            identityRepository.findById(identityRequest.getId()).toFuture().get().getPassword()
                    )
                    .email(identityRequest.getEmail())
                    .authorityId(1L)
                    .enabled(true)
                    .isNew(false)
                    .build();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public IdentityResponse toIdentityResponse(Identity identity) {
        try {
            return IdentityResponse.builder()
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
                    .build();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
