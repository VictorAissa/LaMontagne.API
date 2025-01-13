package com.victor.lamontagne_api.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor public class UserDTO {
    @NonNull
    private String id;

    @NotBlank
    @NonNull
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    @NonNull
    private String name;
}
