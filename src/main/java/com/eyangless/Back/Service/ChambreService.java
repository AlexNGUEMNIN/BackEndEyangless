package com.eyangless.Back.Service;

import com.eyangless.Back.DTO.ChambreDTO;
import com.eyangless.Back.Entity.Chambre;

import java.util.List;
import java.util.Map;

public interface ChambreService {
    ChambreDTO createChambre(ChambreDTO dto, String authHeader);
    List<Chambre> getChambresByCite(String citeId, int pageNo, int pageSize);
    Chambre getChambreById(String id);
    Map<String, Object> updateChambre(ChambreDTO dto, String authHeader);
    Map<String, Object> deleteChambre(String id, String authHeader);
    Map<String, Object> changeStatus(String id, String authHeader);
}