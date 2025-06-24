package com.eyangless.Back.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Data
public class Chambre {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    @OneToMany
    private List<Note> notes;
    @ManyToOne
    private Groupe groupe;
    @OneToMany(mappedBy = "chambre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations;
    @ManyToOne
    @JsonBackReference
    private Bailleur bailleur;
    @ManyToOne
    @JsonBackReference
    private Cite cite;
    private Boolean is_disponible;
    @CreationTimestamp
    private Date created_at;
    @UpdateTimestamp
    private Date updated_at;

    @PrePersist
    protected void onCreate() {
        is_disponible = true;
    }
}
