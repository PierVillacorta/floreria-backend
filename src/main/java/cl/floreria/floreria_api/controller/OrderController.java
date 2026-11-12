package cl.floreria.floreria_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


import cl.floreria.floreria_api.dto.CreateOrderRequest;
import cl.floreria.floreria_api.dto.OrderResponse;
import cl.floreria.floreria_api.dto.OrderSummaryResponse;
import cl.floreria.floreria_api.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Órdenes", description = "Compras (boletas) de los clientes")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Realiza una compra",
               description = "Valida el stock, cobra y registra la boleta. "
                       + "Si el pago falla la orden queda con status FAILED y no se descuenta stock.")
    @ApiResponse(responseCode = "201", description = "Orden registrada (revisar el campo status)")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o producto inexistente")
    @ApiResponse(responseCode = "409", description = "Stock insuficiente")
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request,
                                @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        return orderService.create(request, Long.valueOf(jwt.getSubject()));
    }

    @GetMapping
    @Operation(summary = "Lista todas las órdenes (ADMIN y SELLER)")
    @ApiResponse(responseCode = "403", description = "Solo administradores y vendedores")
    public List<OrderSummaryResponse> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/me")
    @Operation(summary = "Lista las órdenes del usuario autenticado")
    public List<OrderSummaryResponse> findMine(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        return orderService.findMine(Long.valueOf(jwt.getSubject()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de una orden",
               description = "Un cliente solo puede ver las suyas; ADMIN y SELLER ven cualquiera.")
    @ApiResponse(responseCode = "404", description = "La orden no existe o no es tuya")
    public OrderResponse findById(@PathVariable Long id,
                                  @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        return orderService.findById(id, Long.valueOf(jwt.getSubject()), jwt.getClaimAsString("role"));
    }
}