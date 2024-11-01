package org.burgas.subscriptionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private Long id;
    private String title;
    private IdentityResponse identityResponse;
    private String created;
    private String updated;
    private String ended;
    private Boolean active;
    private Boolean paid;
}
