package com.library.model;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable=false)
    private String password; // NOTE: plaintext for demo — replace with hashing in production

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Role role;

    public User() {}
    public User(String name, String email, String password, Role role) {
        this.name = name; this.email = email; this.password = password; this.role = role;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name='" + name + '\'' +
                ", email='" + email + '\'' + ", role=" + role + '}';
    }
}
