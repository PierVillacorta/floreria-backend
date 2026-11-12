package cl.floreria.floreria_api.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


public record CreateOrderRequest(

        @JsonProperty("customer_name")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String customerName,

        @JsonProperty("customer_email")
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es válido")
        @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
        String customerEmail,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 300, message = "La dirección no puede superar los 300 caracteres")
        String address,

        @NotBlank(message = "La región es obligatoria")
        @Size(max = 100, message = "La región no puede superar los 100 caracteres")
        String region,

        @NotBlank(message = "La comuna es obligatoria")
        @Size(max = 100, message = "La comuna no puede superar los 100 caracteres")
        String commune,

        @Size(max = 500, message = "Las indicaciones no pueden superar los 500 caracteres")
        String notes,

        @NotEmpty(message = "La orden debe tener al menos un producto")
        @Valid
        List<OrderItemRequest> items) {

    public record OrderItemRequest(
            @JsonProperty("product_id")
            @NotNull(message = "El producto es obligatorio")
            Long productId,

            @NotNull(message = "La cantidad es obligatoria")
            @Min(value = 1, message = "La cantidad mínima es 1")
            Integer quantity) {
    }
}