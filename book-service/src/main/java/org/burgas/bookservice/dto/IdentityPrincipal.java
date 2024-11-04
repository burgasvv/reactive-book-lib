package org.burgas.bookservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityPrincipal {

    private Long id;
    private String username;
    private String password;
    private List<String> authorities;
    private Boolean enabled;
    private Boolean isAuthenticated;
}
