package com.victor.lamontagne_api.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
public class UserDTO {
    private String id;

    @NotBlank
    @NonNull
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    @NonNull
    private String name;

    public UserDTO(String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
}
