package cl.floreria.floreria_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import cl.floreria.floreria_api.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId, Sort sort);
    
    @EntityGraph(attributePaths = "items")
    Optional<Order> findWithItemsById(Long id);

    boolean existsByUserId(Long userId);
}