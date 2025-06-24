package com.eyangless.Back.Service;

import com.eyangless.Back.DTO.UserDTO;
import com.eyangless.Back.Entity.Moderateur;

import java.util.List;

public interface ModerateurService {
    public Moderateur save(UserDTO moderateur);

    Moderateur update(String id, Moderateur moderateur);
    void delete(String id);
    Moderateur getById(String id);
    List<Moderateur> getAll();
}
