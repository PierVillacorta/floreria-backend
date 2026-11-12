package cl.floreria.floreria_api.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import cl.floreria.floreria_api.entity.Order;

public record OrderSummaryResponse(
        @JsonProperty("order_id") Long orderId,
        String status,
        BigDecimal total,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("customer_name") String customerName,
        @JsonProperty("customer_email") String customerEmail) {

    public static OrderSummaryResponse from(Order o) {
        return new OrderSummaryResponse(
                o.getId(), o.getStatus(), o.getTotal(), o.getCreatedAt(),
                o.getCustomerName(), o.getCustomerEmail());
    }
}