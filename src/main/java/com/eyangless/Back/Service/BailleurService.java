package com.eyangless.Back.Service;

import com.eyangless.Back.DTO.UserDTO;
import com.eyangless.Back.Entity.Bailleur;
import com.eyangless.Back.Entity.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BailleurService {
    public Bailleur findByEmail(String email);
    public Bailleur save(UserDTO bailleur);

    Map<String, Object> delete(String id, String autheader);

    User updateUser(String id, User updatedUser);
    List<Bailleur> findAll();

    Optional<Bailleur> findById(String id);

    List<Bailleur> searchByNom(String nom);

    Bailleur suspendUser(String id);
}
