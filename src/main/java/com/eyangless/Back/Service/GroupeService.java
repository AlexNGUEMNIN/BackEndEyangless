package com.eyangless.Back.Service;



import com.eyangless.Back.DTO.GroupeDTO;
import com.eyangless.Back.Entity.Groupe;

import java.util.List;
import java.util.Map;

public interface GroupeService {
    GroupeDTO createGroupe(GroupeDTO dto, String autheader);
    Map<String, Object> updateGroupe(GroupeDTO dto, String autheader);
    Groupe getGroupeById(String id);
    List<Groupe> getGroupesByCite(String citeId, int pageNo, int pageSize);
    String deleteGroupe(String id, String autheader);
}