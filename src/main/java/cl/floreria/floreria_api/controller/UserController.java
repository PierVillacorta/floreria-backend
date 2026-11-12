package cl.floreria.floreria_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


import cl.floreria.floreria_api.dto.CreateUserRequest;
import cl.floreria.floreria_api.dto.UpdateUserRequest;
import cl.floreria.floreria_api.dto.UserResponse;
import cl.floreria.floreria_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuarios", description = "Administración de usuarios (solo ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Lista todos los usuarios")
    @ApiResponse(responseCode = "403", description = "Solo un administrador puede ver los usuarios")
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un usuario con el rol indicado")
    @ApiResponse(responseCode = "201", description = "Usuario creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "El correo ya está registrado")
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edita nombre, rol y estado de un usuario")
    @ApiResponse(responseCode = "404", description = "El usuario no existe")
    @ApiResponse(responseCode = "409", description = "Un admin no puede degradar ni desactivar su propia cuenta")
    public UserResponse update(@PathVariable Long id,
                               @Valid @RequestBody UpdateUserRequest request,
                               @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        return userService.update(id, request, Long.valueOf(jwt.getSubject()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina un usuario")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado")
    @ApiResponse(responseCode = "404", description = "El usuario no existe")
    @ApiResponse(responseCode = "409", description = "Un admin no puede eliminar su propia cuenta")
    public void delete(@PathVariable Long id,
                       @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        userService.delete(id, Long.valueOf(jwt.getSubject()));
    }
}