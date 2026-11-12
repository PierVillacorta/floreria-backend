package cl.floreria.floreria_api.service;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.floreria.floreria_api.dto.*;
import cl.floreria.floreria_api.entity.*;
import cl.floreria.floreria_api.exception.*;
import cl.floreria.floreria_api.repository.*;


@Service
public class OrderService {

    private static final String PAID = "PAID";
    private static final String FAILED = "FAILED";

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PaymentSimulator paymentSimulator;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository,
                        PaymentSimulator paymentSimulator) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.paymentSimulator = paymentSimulator;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        List<Long> ids = request.items().stream()
                .map(CreateOrderRequest.OrderItemRequest::productId)
                .toList();
        if (new HashSet<>(ids).size() != ids.size()) {
            throw new BadRequestException("Hay productos repetidos en la orden");
        }

        Map<Long, Product> products = productRepository.findAllByIdForUpdate(ids).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderItemRequest item : request.items()) {
            Product product = products.get(item.productId());
            if (product == null) {
                throw new BadRequestException("El producto " + item.productId() + " no existe");
            }
            if (product.getStock() < item.quantity()) {
                throw new ConflictException("Stock insuficiente para " + product.getName()
                        + " (disponible: " + product.getStock() + ")");
            }
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.quantity())));
        }

        boolean paid = paymentSimulator.charge(total);
        Order order = new Order(
                user,
                request.customerName().trim(),
                request.customerEmail().trim(),
                request.address().trim(),
                request.region().trim(),
                request.commune().trim(),
                blankToNull(request.notes()),
                total,
                paid ? PAID : FAILED);

        for (CreateOrderRequest.OrderItemRequest item : request.items()) {
            Product product = products.get(item.productId());
            order.addItem(new OrderItem(product, product.getName(), product.getPrice(), item.quantity()));
            if (paid) {
                product.decreaseStock(item.quantity());
            }
        }

        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> findAll() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(OrderSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> findMine(Long userId) {
        return orderRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(OrderSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id, Long userId, String role) {
        Order order = orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new NotFoundException("Orden no encontrada"));

        boolean staff = "ADMIN".equals(role) || "SELLER".equals(role);
        if (!staff && !order.getUser().getId().equals(userId)) {
            throw new NotFoundException("Orden no encontrada");
        }
        return OrderResponse.from(order);
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}