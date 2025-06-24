package com.eyangless.Back.ServiceImpl;


import com.eyangless.Back.DTO.GroupeDTO;
import com.eyangless.Back.Entity.*;
import com.eyangless.Back.Repository.*;
import com.eyangless.Back.Service.GroupeService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupeServiceImpl implements GroupeService {

    private final GroupeRepository groupeRepository;
    private final CiteRepository citeRepository;
    private final BailleurRepository bailleurRepository;
    private final CaracteristiqueRepository caracteristiqueRepository;
    private final UserServiceImpl userService;
    private final ChambreRepository chambreRepository;

    @Override
    public GroupeDTO createGroupe(GroupeDTO dto, String autheader) {
        log.info("test create groupe");
        Claims claims = userService.extractAllClaims(autheader);
        Cite cite = citeRepository.findCiteById(dto.getCiteId());
        Bailleur bailleur = bailleurRepository.findBailleursById(String.valueOf(claims.get("id_utilisateur")));
        Groupe groupe = new Groupe();
        List<File> files = new ArrayList<>();
        for (File file:dto.getPicturePaths()) {
            files.add(file);
        }
        groupe.setPictures(files);
        groupe.setSuperficie(dto.getSuperficie());
        groupe.setCite(cite);
        groupe.setBailleur(bailleur);
        Groupe groupe1 = groupeRepository.save(groupe);
        cite.getGroupes().add(groupe1);
        citeRepository.save(cite);
        List<Caracteristique> caracteristiques = new ArrayList<>();
        for (Caracteristique caracteristique:dto.getCaracteristiques()) {
            Caracteristique caracteristique1 = caracteristiqueRepository.save(caracteristique);
            caracteristiques.add(caracteristique1);
        }
        groupe1.setCaracteristiques(caracteristiques);
        groupe1.setId(groupe1.getId());

        return toDTO(groupeRepository.save(groupe1));
    }

    @Override
    public Map<String, Object> updateGroupe(GroupeDTO dto, String autheader) {
        Map<String, Object> response = new HashMap<>();
        Claims claims = userService.extractAllClaims(autheader);
        String id_bailleur = (String) claims.get("id_utilisateur");
        List<Map<String, Object>> roles = (List<Map<String, Object>>) claims.get("roles");
        String role = roles.get(0).get("libelle").toString();
        Groupe groupe = groupeRepository.findById(dto.getId()).orElseThrow();
        if (role.compareTo("Bailleur") != 0 || groupe.getBailleur().getId().compareTo(id_bailleur) != 0) {
            response.put("status", "ACCESS DENIED");
            return response;
        }
        groupe.setSuperficie(dto.getSuperficie());
        List<Caracteristique> caracteristiques = new ArrayList<>();
        for (Caracteristique caracteristique: dto.getCaracteristiques()) {
            //            verifier si la caracteristique existe et si elle est une des caracteristique du groupe avant de la modifier
//            Caracteristique caracteristique1 = caracteristiqueRepository.findCaracteristiqueById(caracteristique.getId());
            Boolean is_groupitem = false;
            Date date = new Date();
            for (Caracteristique caracteristique2:groupe.getCaracteristiques()) {
                if (caracteristique2.getId().equals(caracteristique.getId())) {
                    is_groupitem = true;
                    date = caracteristique2.getCreated_at();
                    break;
                }
            }
            if (!is_groupitem) {
                response.put("status", "ACCESS DENIED");
                return response;
            }
            caracteristique.setCreated_at(date);
            Caracteristique caracteristique2 = caracteristiqueRepository.save(caracteristique);
        }
        response.put("success", groupeRepository.save(groupe));
        return response;
    }

    @Override
    public Groupe getGroupeById(String id) {
        return groupeRepository.findById(id).orElseThrow();
    }

    @Override
    public List<Groupe> getGroupesByCite(String citeId, int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        return groupeRepository.findByCiteId(citeId, paging).stream().collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String deleteGroupe(String id, String autheader) {
        Claims claims = userService.extractAllClaims(autheader);
        String id_bailleur = (String) claims.get("id_utilisateur");
        List<Map<String, Object>> roles = (List<Map<String, Object>>) claims.get("roles");
        String role = roles.get(0).get("libelle").toString();
        Groupe groupe = groupeRepository.findGroupeById(id);
        if (groupe == null || role.compareTo("Bailleur") != 0 || groupe.getBailleur().getId().compareTo(id_bailleur) != 0) {
            return "ACCESS DENIED";
        }
        List<Chambre> chambres = chambreRepository.findChambreByGroupe(groupe);
        for (Chambre chambre: chambres) {
            chambreRepository.delete(chambre);
        }
        groupeRepository.deleteById(id);
        return "DELETE SUCCESSFULL";
    }

    private GroupeDTO toDTO(Groupe groupe) {
        GroupeDTO dto = new GroupeDTO();
        dto.setId(groupe.getId());
        dto.setSuperficie(groupe.getSuperficie());
        dto.setCiteId(groupe.getCite().getId());
//        dto.setBailleurId(groupe.getBailleur().getId());
        // TODO: set caracteristiqueIds and picturePaths if applicable
        return dto;
    }
}