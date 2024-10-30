package org.burgas.identityservice.mapper;

import lombok.RequiredArgsConstructor;
import org.burgas.identityservice.dto.AuthorityRequest;
import org.burgas.identityservice.dto.AuthorityResponse;
import org.burgas.identityservice.entity.Authority;
import org.burgas.identityservice.repository.AuthorityRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AuthorityMapper {

    private final AuthorityRepository authorityRepository;

    public Authority toAuthority(AuthorityRequest authorityRequest) {
        try {
            return Authority.builder()
                    .id(authorityRequest.getId())
                    .name(authorityRequest.getName())
                    .isNew(
                            authorityRepository.findById(authorityRequest.getId()).toFuture().get() == null
                    )
                    .build();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public AuthorityResponse toAuthorityResponse(Authority authority) {
        return AuthorityResponse.builder()
                .id(authority.getId())
                .name(authority.getName())
                .build();
    }
}
