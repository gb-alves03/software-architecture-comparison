package com.tcc.banking_app_monolith.account_management.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "owners")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "phone", nullable = false)
    private String phone;

    public void validate() {
        if (name == null || name.isBlank()) {
            throw new RuntimeException("Name cannot be a null");
        }
        if (email == null || email.isBlank() || !email.matches("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$")) {
            throw new RuntimeException("Invalid email");
        }
        if (phone == null || phone.isBlank() || !phone.matches("\\+?[0-9]{10,15}")) {
            throw new RuntimeException("Invalid phone");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
