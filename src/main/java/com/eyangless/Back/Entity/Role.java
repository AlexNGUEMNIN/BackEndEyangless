package com.eyangless.Back.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(unique = true)
    private String libelle;

    @ManyToMany(mappedBy = "roles")
    @JsonBackReference
    private Set<User> utilisateurSet = new HashSet<>();

    @Override
    public String getAuthority() {
        return this.libelle;
    }

    public void addUtilisateur(User utilisateur) {
        if (this.utilisateurSet == null) {
            this.utilisateurSet = new HashSet<>();
        }
        this.utilisateurSet.add(utilisateur);
//        utilisateur.getRoles().add(this);
    }

    public Role(String libelle, Set<User> utilisateurSet) {
        this.utilisateurSet = utilisateurSet;
        this.libelle = libelle;
//        this.id = id;
    }

    public Role() {
    }


    public String getId() {
        return this.id;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public Set<User> getUtilisateurSet() {
        return this.utilisateurSet;
    }
}
