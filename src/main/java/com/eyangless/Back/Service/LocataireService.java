package com.eyangless.Back.Service;

import com.eyangless.Back.DTO.UserDTO;
import com.eyangless.Back.Entity.Locataire;

import java.util.List;
import java.util.Map;

public interface LocataireService {
    public Locataire save(UserDTO locataire);
    Locataire update(String id, Locataire locataire);
    Map<String, Object> delete(String id, String autheader);
    Locataire getById(String id);
    List<Locataire> getAll();
}
