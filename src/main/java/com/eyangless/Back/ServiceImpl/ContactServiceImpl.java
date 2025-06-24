package com.eyangless.Back.ServiceImpl;

import com.eyangless.Back.DTO.ContactDTO;
import com.eyangless.Back.Entity.Cite;
import com.eyangless.Back.Entity.Contact;
import com.eyangless.Back.Repository.CiteRepository;
import com.eyangless.Back.Repository.ContactRepository;
import com.eyangless.Back.Service.ContactService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {
    private final UserServiceImpl userService;
    private final CiteRepository citeRepository;
    private final ContactRepository contactRepository;
    @Override
    public Map<String, Object> addContact(String citeid, List<ContactDTO> dtos, String autheader) {
        Map<String, Object> response = new HashMap<>();
        Claims claims = userService.extractAllClaims(autheader);
        String id_bailleur = (String) claims.get("id_utilisateur");
        List<Map<String, Object>> roles = (List<Map<String, Object>>) claims.get("roles");
        String role = roles.get(0).get("libelle").toString();
        Cite cite = citeRepository.findCiteById(citeid);
        if (cite == null) {
            response.put("message", "cette cite n'existe pas");
            return response;
        }
        if (role.compareTo("Bailleur") != 0 || cite.getBailleur().getId().compareTo(id_bailleur) != 0) {
            return null;
        }
        for (ContactDTO contactDTO : dtos) {
            Contact contact = new Contact();
            contact.setName(contactDTO.getName());
            contact.setPhone(contactDTO.getPhone());
            contact.setEmail(contactDTO.getEmail());
            Contact contact1 = contactRepository.save(contact);
            cite.getContacts().add(contact1);
        }
        citeRepository.save(cite);
        response.put("message", "contacts enregistrer avec success");
        response.put("all contact", cite.getContacts());
        return response;
    }
}
