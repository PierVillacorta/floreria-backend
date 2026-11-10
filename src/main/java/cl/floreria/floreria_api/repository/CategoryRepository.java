package cl.floreria.floreria_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.floreria.floreria_api.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}