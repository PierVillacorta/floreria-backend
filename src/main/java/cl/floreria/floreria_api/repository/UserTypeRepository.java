package cl.floreria.floreria_api.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.floreria.floreria_api.entity.UserType;

public interface UserTypeRepository extends JpaRepository<UserType, Long> {

    Optional<UserType> findByName(String name);
}