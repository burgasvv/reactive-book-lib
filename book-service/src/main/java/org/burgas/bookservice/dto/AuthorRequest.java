package org.burgas.bookservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRequest {

    private Long id;
    private String firstname;
    private String lastname;
    private String patronymic;
}
