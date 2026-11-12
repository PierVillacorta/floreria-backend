package cl.floreria.floreria_api.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.floreria.floreria_api.dto.CreateUserRequest;
import cl.floreria.floreria_api.dto.UpdateUserRequest;
import cl.floreria.floreria_api.dto.UserResponse;
import cl.floreria.floreria_api.entity.User;
import cl.floreria.floreria_api.entity.UserType;
import cl.floreria.floreria_api.exception.BadRequestException;
import cl.floreria.floreria_api.exception.ConflictException;
import cl.floreria.floreria_api.exception.NotFoundException;
import cl.floreria.floreria_api.repository.OrderRepository;
import cl.floreria.floreria_api.repository.UserRepository;
import cl.floreria.floreria_api.repository.UserTypeRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepository  orderRepository;

    public UserService(UserRepository userRepository,
                       UserTypeRepository userTypeRepository,
                       PasswordEncoder passwordEncoder,
                        OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.passwordEncoder = passwordEncoder;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll(Sort.by("id")).stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("El correo ya está registrado");
        }
        UserType type = findType(request.role());

        User user = new User(
                request.fullName().trim(),
                request.email(),
                passwordEncoder.encode(request.password()),
                type,
                request.address().trim(),
                request.region().trim(),
                request.commune().trim());

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request, Long currentUserId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        UserType type = findType(request.role());

        // El admin no puede ni debe quitarse el rol ni desactivar su propia cuenta
        if (id.equals(currentUserId)
                && (!"ADMIN".equals(type.getName()) || !"ACTIVE".equals(request.accountStatus()))) {
            throw new ConflictException(
                    "No puedes quitarte el rol de administrador ni desactivar tu propia cuenta");
        }

        user.update(request.fullName().trim(), type, request.accountStatus());
        return UserResponse.from(user);
    }

    @Transactional
    public void delete(Long id, Long currentUserId) {
    if (id.equals(currentUserId)) {
        throw new ConflictException("No puedes eliminar tu propia cuenta");
    }
    if (!userRepository.existsById(id)) {
        throw new NotFoundException("Usuario no encontrado");
    }
    if (orderRepository.existsByUserId(id)) {
        throw new ConflictException(
                "No se puede eliminar: el usuario tiene compras registradas. Puedes desactivarlo en su lugar");
    }
    userRepository.deleteById(id);
    }

    private UserType findType(String name) {
        return userTypeRepository.findByName(name)
                .orElseThrow(() -> new BadRequestException("El rol indicado no existe"));
    }
}