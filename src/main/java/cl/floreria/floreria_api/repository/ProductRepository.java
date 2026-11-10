package cl.floreria.floreria_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.floreria.floreria_api.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    long countByCategoryId(Long categoryId);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
}