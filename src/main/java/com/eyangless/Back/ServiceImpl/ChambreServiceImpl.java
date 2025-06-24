package com.eyangless.Back.ServiceImpl;


import com.eyangless.Back.DTO.ChambreDTO;
import com.eyangless.Back.Entity.*;
import com.eyangless.Back.Repository.BailleurRepository;
import com.eyangless.Back.Repository.ChambreRepository;
import com.eyangless.Back.Repository.CiteRepository;
import com.eyangless.Back.Repository.GroupeRepository;
import com.eyangless.Back.Service.ChambreService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChambreServiceImpl implements ChambreService {

    private final ChambreRepository chambreRepository;
    private final CiteRepository citeRepository;
    private final GroupeRepository groupeRepository;
    private final BailleurRepository bailleurRepository;
    private final UserServiceImpl userService;


    @Override
    public ChambreDTO createChambre(ChambreDTO dto, String authHeader) {
        Claims claims = userService.extractAllClaims(authHeader);
        List<Map<String, Object>> roles = (List<Map<String, Object>>) claims.get("roles");
        String role = roles.get(0).get("libelle").toString();
        if (role.compareTo("Bailleur") != 0 )
            return null;
        Cite cite = citeRepository.findCiteById(dto.getCiteId());
        Groupe groupe = groupeRepository.findGroupeById(dto.getGroupeId());
        Bailleur bailleur = bailleurRepository.findBailleursById((String) claims.get("id_utilisateur"));
        Chambre chambre = new Chambre();
        chambre.setCite(cite);
        chambre.setGroupe(groupe);
        chambre.setBailleur(bailleur);
        Chambre chambre1 = chambreRepository.save(chambre);
        cite.getChambres().add(chambre1);
        citeRepository.save(cite);
        return toDTO(chambre1);
    }

    @Override
    public List<Chambre> getChambresByCite(String citeId, int pageNo, int pagesize) {
        Pageable paging = PageRequest.of(pageNo, pagesize);
        return chambreRepository.findChambreByCiteId(citeId, paging);
    }


    @Override
    public Chambre getChambreById(String id) {
        return chambreRepository.findChambreById(id);
    }

    @Override
    public Map<String, Object> deleteChambre(String id, String autheader) {
        Map<String, Object> response = new HashMap<>();
        Claims claims = userService.extractAllClaims(autheader);
        String id_bailleur = (String) claims.get("id_utilisateur");
        List<Map<String, Object>> roles = (List<Map<String, Object>>) claims.get("roles");
        String role = roles.get(0).get("libelle").toString();
        Chambre chambre = chambreRepository.findChambreById(id);
        if (role.compareTo("Bailleur") != 0 || chambre.getBailleur().getId().compareTo(id_bailleur) != 0) {
            response.put("status", "ACCESS DENIED");
            return response;
        }
        chambreRepository.delete(chambre);
        response.put("status", "delete successfully");
        return response;
    }

    @Override
    public Map<String, Object> changeStatus(String id, String authHeader) {
        Map<String, Object> response = new HashMap<>();
        Claims claims = userService.extractAllClaims(authHeader);
        String id_bailleur = (String) claims.get("id_utilisateur");
        List<Map<String, Object>> roles = (List<Map<String, Object>>) claims.get("roles");
        String role = roles.get(0).get("libelle").toString();
        Chambre chambre = chambreRepository.findChambreById(id);
        if (role.compareTo("Bailleur") != 0 || chambre.getBailleur().getId().compareTo(id_bailleur) != 0) {
            response.put("status", "ACCESS DENIED");
            return response;
        }
        chambre.setIs_disponible(!chambre.getIs_disponible());
        chambreRepository.save(chambre);
        response.put("message", "status change successful");
        return response;
    }

    @Override
    public Map<String, Object> updateChambre(ChambreDTO dto, String autheader) {
        Map<String, Object> response = new HashMap<>();
        Claims claims = userService.extractAllClaims(autheader);
        String id_bailleur = (String) claims.get("id_utilisateur");
        List<Map<String, Object>> roles = (List<Map<String, Object>>) claims.get("roles");
        String role = roles.get(0).get("libelle").toString();
        Chambre chambre = chambreRepository.findChambreById(dto.getId());
        if (role.compareTo("Bailleur") != 0 || chambre.getBailleur().getId().compareTo(id_bailleur) != 0) {
            response.put("status", "ACCESS DENIED");
            return response;
        }
        Groupe groupe = groupeRepository.findGroupeById(dto.getGroupeId());
        if (groupe == null ) {
            response.put("status", "cannot find groupe");
            return response;
        }
        chambre.setGroupe(groupe);
        chambreRepository.save(chambre);
        response.put("status", "updated successfully");
        return response;
    }



    private ChambreDTO toDTO(Chambre chambre) {
        ChambreDTO dto = new ChambreDTO();
        dto.setId(chambre.getId());
        dto.setCiteId(chambre.getCite().getId());
        dto.setGroupeId(chambre.getGroupe().getId());
//        dto.setBailleurId(chambre.getBailleur().getId());
        return dto;
    }
}

