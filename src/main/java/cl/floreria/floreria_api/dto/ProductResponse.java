package cl.floreria.floreria_api.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import cl.floreria.floreria_api.entity.Product;

public record ProductResponse(
        @JsonProperty("product_id") Long productId,
        @JsonProperty("product_code") String productCode,
        @JsonProperty("product_name") String productName,
        @JsonProperty("image_url") String imageUrl,
        Integer stock,
        BigDecimal price,
        String description,
        @JsonProperty("category_id") Long categoryId,
        @JsonProperty("critical_stock") Integer criticalStock) {

    public static ProductResponse from(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getCode(),
                p.getName(),
                p.getImageUrl(),
                p.getStock(),
                p.getPrice(),
                p.getDescription(),
                p.getCategory().getId(),
                p.getCriticalStock());
    }
}