package cl.floreria.floreria_api.config;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import cl.floreria.floreria_api.entity.User;
import cl.floreria.floreria_api.entity.UserType;
import cl.floreria.floreria_api.repository.UserRepository;
import cl.floreria.floreria_api.repository.UserTypeRepository;

@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private record SeedUser(String fullName, String email, String password, String role) {
    }
    //datos de demostracion
    private static final List<SeedUser> USERS = List.of(
            new SeedUser("Administrador", "admin@admin.com", "admin123", "ADMIN"),
            new SeedUser("Vendedor", "vendedor@floreria.com", "vendedor", "SELLER"),
            new SeedUser("Cliente de Prueba", "cliente@gmail.com", "cliente123", "CUSTOMER"));

    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean enabled;

    public DataSeeder(UserRepository userRepository,
                      UserTypeRepository userTypeRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${app.seed.enabled:true}") boolean enabled) {
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            return;
        }
        for (SeedUser seed : USERS) {
            if (userRepository.existsByEmail(seed.email())) {
                continue;
            }
            UserType type = userTypeRepository.findByName(seed.role())
                    .orElseThrow(() -> new IllegalStateException("Falta el tipo de usuario " + seed.role()));

            userRepository.save(new User(
                    seed.fullName(),
                    seed.email(),
                    passwordEncoder.encode(seed.password()),
                    type,
                    "Av. Providencia 1234",
                    "Región Metropolitana",
                    "Providencia"));

            log.info("Usuario de demostración creado: {} ({})", seed.email(), seed.role());
        }
    }
}