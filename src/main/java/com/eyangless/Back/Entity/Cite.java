package com.eyangless.Back.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Data
public class Cite {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    @Column(unique = true)
    private String name;
    @Lob
    private String description;
    @OneToMany(mappedBy = "cite", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Groupe> groupes;
    @OneToMany(mappedBy = "cite", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chambre> chambres;
    @OneToMany
//    @JsonManagedReference("contact_cite")
    private List<Contact> contacts;
    @OneToMany
//    @JsonManagedReference("caracteristique_cite")
    private List<Caracteristique> suplements;
    @OneToMany
    private List<Note> notes;
    @ManyToOne
    private Localisation localisation;
    @ManyToOne
    private Bailleur bailleur;
    @CreationTimestamp
    private Date created_at;
    @UpdateTimestamp
    private Date updated_at;
}
