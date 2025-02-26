package com.victor.lamontagne_api.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MeteoRequest(@NotNull Double latitude, @NotNull Double longitude, @NotBlank String date) {
}
