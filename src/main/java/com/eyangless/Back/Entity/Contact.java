package com.eyangless.Back.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Data
public class Contact {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    private String name;
    @Column(unique = true)
    private String phone;
    @Column(unique = true)
    private String email;
//    @ManyToOne
//    @JsonBackReference("contact_cite")
//    private Cite cite;
    @CreationTimestamp
    private Date created_at;
    @UpdateTimestamp
    private Date updated_at;
}
