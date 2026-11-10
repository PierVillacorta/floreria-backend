package cl.floreria.floreria_api.service;
import cl.floreria.floreria_api.exception.ConflictException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.floreria.floreria_api.dto.ProductRequest;
import cl.floreria.floreria_api.dto.ProductResponse;
import cl.floreria.floreria_api.entity.Category;
import cl.floreria.floreria_api.entity.Product;
import cl.floreria.floreria_api.exception.BadRequestException;
import cl.floreria.floreria_api.exception.NotFoundException;
import cl.floreria.floreria_api.repository.CategoryRepository;
import cl.floreria.floreria_api.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
    Category category = findCategory(request.categoryId());

    if (productRepository.existsByCode(request.code())) {
        throw new ConflictException("Ya existe un producto con ese código");
    }

    Product product = new Product(
            request.code(),
            request.name().trim(),
            blankToNull(request.imageUrl()),
            request.stock(),
            request.price(),
            blankToNull(request.description()),
            category,
            orZero(request.criticalStock()));

    return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    Category category = findCategory(request.categoryId());

    if (productRepository.existsByCodeAndIdNot(request.code(), id)) {
        throw new ConflictException("Ya existe otro producto con ese código");
    }

    product.update(
            request.code(),
            request.name().trim(),
            blankToNull(request.imageUrl()),
            request.stock(),
            request.price(),
            blankToNull(request.description()),
            category,
            orZero(request.criticalStock()));

    return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Producto no encontrado");
        }
        productRepository.deleteById(id);
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("La categoría indicada no existe"));
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private static Integer orZero(Integer value) {
        return value == null ? 0 : value;
    }
}