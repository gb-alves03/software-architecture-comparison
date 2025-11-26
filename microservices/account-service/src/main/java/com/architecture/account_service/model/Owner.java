package com.architecture.account_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "owners")
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ownerId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", unique = true)
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

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
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
