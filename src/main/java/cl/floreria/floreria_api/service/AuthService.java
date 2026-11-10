package cl.floreria.floreria_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.floreria.floreria_api.dto.*;
import cl.floreria.floreria_api.entity.User;
import cl.floreria.floreria_api.entity.UserType;
import cl.floreria.floreria_api.exception.ConflictException;
import cl.floreria.floreria_api.exception.UnauthorizedException;
import cl.floreria.floreria_api.repository.UserRepository;
import cl.floreria.floreria_api.repository.UserTypeRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       UserTypeRepository userTypeRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("El correo ya está registrado");
        }
        UserType customer = userTypeRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new IllegalStateException("Falta el tipo de usuario CUSTOMER"));

        User user = new User(
            request.fullName().trim(),
            request.email(),
            passwordEncoder.encode(request.password()),
            customer,
            request.address().trim(),
            request.region().trim(),
            request.commune().trim());

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new UnauthorizedException("Correo o contraseña incorrectos"));

        if (!"ACTIVE".equals(user.getAccountStatus())) {
            throw new UnauthorizedException("La cuenta está inactiva");
        }

        return new LoginResponse(
                jwtService.generate(user),
                "Bearer",
                jwtService.expiresInSeconds(),
                UserResponse.from(user));
    }
}