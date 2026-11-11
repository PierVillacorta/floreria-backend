package cl.floreria.floreria_api.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @NotBlank(message = "La credencial de Google es obligatoria")
        String credential) {
}