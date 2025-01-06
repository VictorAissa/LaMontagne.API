package com.victor.lamontagne_api.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;

    @NotBlank
    private String email;

    @NotBlank
    private String name;
}
