package ru.onegines.carpark.CarPark.models;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.Set;

/**
 * @author onegines
 * @date 05.11.2024
 */
@Entity
@Table(name = "managers")
public class Manager extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manager_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "manager_enterprise",
            joinColumns = @JoinColumn(name = "manager_id"),
            inverseJoinColumns = @JoinColumn(name = "enterprise_id")
    )
    private Set<Enterprise> enterprises;

    public Manager() {
        super("defaultUsername", "defaultPassword", Collections.emptyList());
    }


    public Manager(String username, String password, Set<GrantedAuthority> authorities, Long id, String role, Set<Enterprise> enterprises) {
        super(username, password, authorities);
        this.id = id;
        this.role = role;
        this.enterprises = enterprises;
    }

    // Getters and Setters
    public Set<Enterprise> getEnterprises() {
        return enterprises;
    }

    public void setEnterprises(Set<Enterprise> enterprises) {
        this.enterprises = enterprises;
    }


    @Override
    public String getUsername() {
        return name;
    }

    public void setUsername(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
