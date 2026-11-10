package cl.floreria.floreria_api.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(length = 9)
    private String run;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_type_id", nullable = false)
    private UserType userType;

    @Column(name = "account_status", nullable = false, length = 10)
    private String accountStatus;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 300)
    private String address;

    @Column(length = 100)
    private String region;

    @Column(length = 100)
    private String commune;

    @Column(length = 15)
    private String phone;

   
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    protected User() {
    }

    public void update(String fullName, UserType userType, String accountStatus) {
        this.fullName = fullName;
        this.userType = userType;
        this.accountStatus = accountStatus;
    }
    public User(String fullName, String email, String passwordHash, UserType userType,
            String address, String region, String commune) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.userType = userType;
        this.accountStatus = "ACTIVE";
        this.address = address;
        this.region = region;
        this.commune = commune;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getLastName() { return lastName; }
    public String getRun() { return run; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public UserType getUserType() { return userType; }
    public String getAccountStatus() { return accountStatus; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getAddress() { return address; }
    public String getRegion() { return region; }
    public String getCommune() { return commune; }
    public String getPhone() { return phone; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}