package cl.floreria.floreria_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import cl.floreria.floreria_api.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    Optional<User> findByGoogleSub(String googleSub);

    @Override
    @EntityGraph(attributePaths = "userType")
    List<User> findAll(Sort sort);
}