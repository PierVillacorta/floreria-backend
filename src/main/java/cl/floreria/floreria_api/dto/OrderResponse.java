package cl.floreria.floreria_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import cl.floreria.floreria_api.entity.Order;

public record OrderResponse(
        @JsonProperty("order_id") Long orderId,
        String status,
        BigDecimal total,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("customer_name") String customerName,
        @JsonProperty("customer_email") String customerEmail,
        String address,
        String region,
        String commune,
        String notes,
        List<Item> items) {

    public record Item(
            @JsonProperty("product_id") Long productId,
            @JsonProperty("product_name") String productName,
            @JsonProperty("unit_price") BigDecimal unitPrice,
            Integer quantity,
            BigDecimal subtotal) {
    }

    public static OrderResponse from(Order o) {
        List<Item> items = o.getItems().stream()
                .map(i -> new Item(
                        i.getProduct().getId(),
                        i.getProductName(),
                        i.getUnitPrice(),
                        i.getQuantity(),
                        i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()))))
                .toList();

        return new OrderResponse(
                o.getId(), o.getStatus(), o.getTotal(), o.getCreatedAt(),
                o.getCustomerName(), o.getCustomerEmail(),
                o.getDeliveryAddress(), o.getDeliveryRegion(), o.getDeliveryCommune(),
                o.getDeliveryNotes(), items);
    }
}