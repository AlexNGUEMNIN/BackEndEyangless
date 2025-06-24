package com.eyangless.Back.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    @Column(unique = true)
    private String email;
    private String telephone;
    @JsonBackReference
    private String password;
    private String nom;
    private String prenom;
    private Boolean status;
    @JsonBackReference
    private String otp;
    @JsonBackReference
    private LocalDateTime otpExpiry;
    @CreationTimestamp
    private Date created_at;
    @UpdateTimestamp
    private Date updated_at;
    private Date login_at;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            inverseJoinColumns = @JoinColumn(name = "numUser", referencedColumnName = "id"),
            joinColumns = @JoinColumn(name = "id")
    )
//    @JsonManagedReference
    private Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Conversion des rôles en GrantedAuthority
        return this.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEnabled() {
        return this.status; // <- c'est ici que Spring bloque si false
    }

    @Override
    public String getUsername() {
        return this.email;
    }
    public Set<Role> getRoles() {
        return roles;
    }
    public String getPassword() {
        return password;
    }
    public void addRole(Role role) {
        if(this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
        role.getUtilisateurSet().add(this);
    }


}
