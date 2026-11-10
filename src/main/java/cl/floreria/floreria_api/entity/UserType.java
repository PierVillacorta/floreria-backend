package cl.floreria.floreria_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_types")
public class UserType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_type_id")
    private Long id;

    @Column(name = "type_name", nullable = false, unique = true, length = 30)
    private String name;

    protected UserType() {
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}