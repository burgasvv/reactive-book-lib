package org.burgas.identityservice.repository;

import org.burgas.identityservice.entity.Identity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IdentityRepository extends R2dbcRepository<Identity, Long> {

    Mono<Identity> findIdentityByUsername(String username);
}
