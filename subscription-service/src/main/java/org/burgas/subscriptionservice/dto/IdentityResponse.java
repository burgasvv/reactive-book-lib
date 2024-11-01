package org.burgas.subscriptionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityResponse {

    private Long id;
    private String username;
    private String password;
    private String email;
    private Boolean enabled;
    private AuthorityResponse authorityResponse;
}
