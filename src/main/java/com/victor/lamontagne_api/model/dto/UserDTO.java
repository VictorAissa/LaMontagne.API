package com.victor.lamontagne_api.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDTO {
    private String id;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String name;
}
