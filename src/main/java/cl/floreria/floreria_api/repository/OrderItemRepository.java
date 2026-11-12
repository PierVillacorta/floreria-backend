package cl.floreria.floreria_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.floreria.floreria_api.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    boolean existsByProductId(Long productId);
}