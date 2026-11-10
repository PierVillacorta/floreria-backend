package cl.floreria.floreria_api.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;


public record UpdateUserRequest(

        @JsonProperty("full_name")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
        String fullName,

        @NotBlank(message = "El rol es obligatorio")
        @Pattern(regexp = "ADMIN|SELLER|CUSTOMER", message = "El rol debe ser ADMIN, SELLER o CUSTOMER")
        String role,

        @JsonProperty("account_status")
        @NotBlank(message = "El estado es obligatorio")
        @Pattern(regexp = "ACTIVE|INACTIVE", message = "El estado debe ser ACTIVE o INACTIVE")
        String accountStatus) {

    public UpdateUserRequest {
        if (role != null) {
            role = role.trim().toUpperCase(Locale.ROOT);
        }
        if (accountStatus != null) {
            accountStatus = accountStatus.trim().toUpperCase(Locale.ROOT);
        }
    }
}