package cl.floreria.floreria_api.controller;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import cl.floreria.floreria_api.dto.*;
import cl.floreria.floreria_api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro e inicio de sesión")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra un nuevo cliente")
    @ApiResponse(responseCode = "201", description = "Cuenta creada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o dominio de correo no permitido")
    @ApiResponse(responseCode = "409", description = "El correo ya está registrado")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Inicia sesión y entrega un token JWT")
    @ApiResponse(responseCode = "401", description = "Credenciales incorrectas o cuenta inactiva")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/google")
    @Operation(summary = "Inicia sesión con una cuenta de Google")
    @ApiResponse(responseCode = "401", description = "Token de Google inválido o cuenta inactiva")
    @ApiResponse(responseCode = "409", description = "Ese correo ya tiene una cuenta con contraseña")
    public LoginResponse google(@Valid @RequestBody GoogleLoginRequest request) {
    return authService.loginWithGoogle(request);
}
}