package cl.floreria.floreria_api.entity;
import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "product_name", nullable = false, length = 100)
    private String name;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "critical_stock", nullable = false)
    private Integer criticalStock;

    protected Product() {
    }

    public Product(String code, String name, String imageUrl, Integer stock, BigDecimal price,
                   String description, Category category, Integer criticalStock) {
        this.code = code;
        this.name = name;
        this.imageUrl = imageUrl;
        this.stock = stock;
        this.price = price;
        this.description = description;
        this.category = category;
        this.criticalStock = criticalStock;
    }

    public void update(String code, String name, String imageUrl, Integer stock, BigDecimal price,
                       String description, Category category, Integer criticalStock) {
        this.code = code;
        this.name = name;
        this.imageUrl = imageUrl;
        this.stock = stock;
        this.price = price;
        this.description = description;
        this.category = category;
        this.criticalStock = criticalStock;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public Integer getStock() { return stock; }
    public BigDecimal getPrice() { return price; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public Integer getCriticalStock() { return criticalStock; }
}