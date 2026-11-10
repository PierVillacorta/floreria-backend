package cl.floreria.floreria_api.dto;
import java.math.BigDecimal;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;

public record ProductRequest(

@JsonProperty("product_code")
@NotBlank(message = "El código es obligatorio")
@Size(min = 3, max = 50, message = "El código debe tener entre 3 y 50 caracteres")
String code,

@JsonProperty("product_name")
@NotBlank(message = "El nombre es obligatorio")
@Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
String name,

@JsonProperty("image_url")
@Size(max = 500, message = "La URL de la imagen no puede superar los 500 caracteres")
String imageUrl,

@NotNull(message = "El stock es obligatorio")
@Min(value = 0, message = "El stock no puede ser negativo")
Integer stock,

@NotNull(message = "El precio es obligatorio")
@DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
@Digits(integer = 8, fraction = 2, message = "El precio admite hasta 8 enteros y 2 decimales")
BigDecimal price,

@Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
String description,

@JsonProperty("category_id")
@NotNull(message = "La categoría es obligatoria")
Long categoryId,

@JsonProperty("critical_stock")
@Min(value = 0, message = "El stock crítico no puede ser negativo")
Integer criticalStock) {
    // Este bloque de código sirve para limpiar y normalizar el código de un producto de forma automática cada vez que se crea un objeto 
    public ProductRequest {
        if (code != null) {
            code = code.trim().toUpperCase(Locale.ROOT);
        }
    }
}