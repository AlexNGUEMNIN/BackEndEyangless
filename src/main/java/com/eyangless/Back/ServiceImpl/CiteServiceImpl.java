package com.eyangless.Back.ServiceImpl;



import com.eyangless.Back.DTO.ChambreDTO;
import com.eyangless.Back.DTO.CiteDTO;

import com.eyangless.Back.DTO.NoteDTO;
import com.eyangless.Back.Entity.*;
import com.eyangless.Back.Repository.*;
import com.eyangless.Back.Service.CiteService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CiteServiceImpl implements CiteService {

    private final CiteRepository citeRepository;
    private final LocalisationRepository localisationRepository;
    private final BailleurRepository bailleurRepository;
    private final UserServiceImpl userService;
    private final ContactRepository contactRepository;
    private final CaracteristiqueRepository caracteristiqueRepository;
    private final NoteRepository noteRepository;

    @Override
    public CiteDTO createCite(ChambreDTO dto) {
        return null;
    }

    @Override
    public CiteDTO createCite(CiteDTO dto, String authHeader) {
        Claims claims = userService.extractAllClaims(authHeader);
        Cite cite = new Cite();
        Bailleur bailleur = bailleurRepository.findBailleursById(String.valueOf(claims.get("id_utilisateur")));
        log.info(bailleur.getNom());
        cite.setBailleur(bailleur);
        Localisation localisation = localisationRepository.save(dto.getLocalisation());
        cite.setLocalisation(localisation);
        cite.setName(dto.getName());
        cite.setDescription(dto.getDescription());
        Cite cite1 = citeRepository.save(cite);
        List<Contact> contacts = new ArrayList<>();
        List<Caracteristique> caracteristiques = new ArrayList<>();
        for (Contact contact: dto.getContactList()) {
//            contact.setCite(cite1);
            contactRepository.save(contact);
            contacts.add(contact);
        }
        for (Caracteristique caracteristique: dto.getSuplementList()) {
//            caracteristique.setCite(cite1);
            caracteristiqueRepository.save(caracteristique);
            caracteristiques.add(caracteristique);
        }
        cite1.setContacts(contacts);
        cite1.setSuplements(caracteristiques);
        return toDTO(citeRepository.save(cite));
    }

    @Override
    public CiteDTO updateCite(CiteDTO dto, String authHeader) {
        log.info(dto.getId());
        Claims claims = userService.extractAllClaims(authHeader);
        String id_bailleur = (String) claims.get("id_utilisateur");
        List<Map<String, Object>> roles = (List<Map<String, Object>>) claims.get("roles");
        String role = roles.get(0).get("libelle").toString();
        Cite cite = citeRepository.findCiteById(dto.getId());
        if (cite == null || role.compareTo("Bailleur") != 0 || cite.getBailleur().getId().compareTo(id_bailleur) != 0) {
            return null;
        }
        cite.setId(dto.getId());
        cite.setName(dto.getName());
        cite.setDescription(dto.getDescription());
        Localisation localisation = localisationRepository.findLocalisationById(dto.getLocalisation().getId());
        if (localisation == null) {
            localisation = localisationRepository.save(dto.getLocalisation());
        }
        cite.setLocalisation(localisation);
        List<Contact> contacts = new ArrayList<>();
        List<Caracteristique> caracteristiques = new ArrayList<>();
        for (Contact contact: dto.getContactList()) {
            Contact contact1 = contactRepository.findContactById(contact.getId());
            if (contact1 == null)
                contactRepository.save(contact);
            contacts.add(contact);
        }
        for (Caracteristique caracteristique: dto.getSuplementList()) {
            Caracteristique caracteristique1 = caracteristiqueRepository.findCaracteristiqueById(caracteristique.getId());
            if (caracteristique1 == null)
                caracteristiqueRepository.save(caracteristique);
            caracteristiques.add(caracteristique);
        }
        cite.setContacts(contacts);
        cite.setSuplements(caracteristiques);
        try {
            cite = citeRepository.save(cite);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toDTO(cite);
    }


    @Override
    public Cite getCiteById(String id) {
        return citeRepository.findById(id).orElseThrow(() -> new RuntimeException("Cite not found"));
    }

    @Override
    public Page<Cite> getAllCites(Integer pageNo, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        return citeRepository.findAll(paging);
    }

    @Override
    public String deleteCite(String id, String authHeader) {
        Claims claims = userService.extractAllClaims(authHeader);
        String id_bailleur = (String) claims.get("id_utilisateur");
        List<Map<String, Object>> roles = (List<Map<String, Object>>) claims.get("roles");
        String role = roles.get(0).get("libelle").toString();
        Cite cite = citeRepository.findCiteById(id);
        if (cite == null || role.compareTo("Bailleur") != 0 || cite.getBailleur().getId().compareTo(id_bailleur) != 0) {
            return "ACCESS DENIED";
        }
        citeRepository.deleteById(id);
        return "DELETE SUCCESSFULL";
    }

    private CiteDTO toDTO(Cite cite) {
        CiteDTO dto = new CiteDTO();
        dto.setId(cite.getId());
        dto.setName(cite.getName());
        dto.setDescription(cite.getDescription());
        return dto;
    }
}