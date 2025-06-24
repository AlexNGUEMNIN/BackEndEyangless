package com.eyangless.Back.DTO;

import com.eyangless.Back.Entity.Caracteristique;
import com.eyangless.Back.Entity.Contact;
import com.eyangless.Back.Entity.Localisation;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import java.util.List;

@Data
public class CiteDTO {
    private String id;
    private String name;
    private String description;
    private Localisation localisation;
    private List<Contact> contactList;
    private List<Caracteristique> suplementList;
}
