package cl.floreria.floreria_api.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.floreria.floreria_api.dto.CategoryRequest;
import cl.floreria.floreria_api.dto.CategoryResponse;
import cl.floreria.floreria_api.entity.Category;
import cl.floreria.floreria_api.exception.ConflictException;
import cl.floreria.floreria_api.exception.NotFoundException;
import cl.floreria.floreria_api.repository.CategoryRepository;
import cl.floreria.floreria_api.repository.ProductRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll(Sort.by("id")).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String name = request.name().trim();

        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException("Ya existe una categoría con ese nombre");
        }
        return CategoryResponse.from(categoryRepository.save(new Category(name)));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
        String name = request.name().trim();

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ConflictException("Ya existe una categoría con ese nombre");
        }
        category.setName(name);
        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Categoría no encontrada");
        }
        long products = productRepository.countByCategoryId(id);
        if (products > 0) {
            throw new ConflictException(
                    "No se puede eliminar: tiene " + products + " producto(s) asociado(s)");
        }
        categoryRepository.deleteById(id);
    }
}