package cl.floreria.floreria_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.floreria.floreria_api.dto.*;
import cl.floreria.floreria_api.entity.User;
import cl.floreria.floreria_api.entity.UserType;
import cl.floreria.floreria_api.exception.BadRequestException;
import cl.floreria.floreria_api.exception.ConflictException;
import cl.floreria.floreria_api.exception.UnauthorizedException;
import cl.floreria.floreria_api.repository.UserRepository;
import cl.floreria.floreria_api.repository.UserTypeRepository;
import cl.floreria.floreria_api.validation.ValidationRules;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GoogleTokenVerifier googleTokenVerifier;

    public AuthService(UserRepository userRepository,
            UserTypeRepository userTypeRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            GoogleTokenVerifier googleTokenVerifier
                ) {
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.googleTokenVerifier = googleTokenVerifier;
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
                .filter(u -> u.getPasswordHash() != null
                        && passwordEncoder.matches(request.password(), u.getPasswordHash()))
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

    @Transactional
    public LoginResponse loginWithGoogle(GoogleLoginRequest request) {
        GoogleTokenVerifier.GoogleIdentity identity = googleTokenVerifier.verify(request.credential());

        // Mismas reglas de dominio que el registro normal
        if (!identity.email().matches(ValidationRules.ALLOWED_EMAIL)) {
            throw new BadRequestException(ValidationRules.ALLOWED_EMAIL_MESSAGE);
        }

        User user = userRepository.findByGoogleSub(identity.sub())
                .orElseGet(() -> createGoogleUser(identity));

        if (!"ACTIVE".equals(user.getAccountStatus())) {
            throw new UnauthorizedException("La cuenta está inactiva");
        }

        return new LoginResponse(
                jwtService.generate(user),
                "Bearer",
                jwtService.expiresInSeconds(),
                UserResponse.from(user));
    }

    private User createGoogleUser(GoogleTokenVerifier.GoogleIdentity identity) {
        if (userRepository.existsByEmail(identity.email())) {
            throw new ConflictException(
                    "Ese correo ya tiene una cuenta con contraseña. Inicia sesión con tu correo y contraseña");
        }
        UserType customer = userTypeRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new IllegalStateException("Falta el tipo de usuario CUSTOMER"));

        String name = (identity.name() == null || identity.name().isBlank())
                ? identity.email().substring(0, identity.email().indexOf('@'))
                : identity.name().trim();
        if (name.length() > 50) {
            name = name.substring(0, 50);
        }

        return userRepository.save(User.fromGoogle(name, identity.email(), identity.sub(), customer));
    }
}