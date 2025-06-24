package com.eyangless.Back.API;

import com.eyangless.Back.DTO.ContactDTO;
import com.eyangless.Back.ServiceImpl.ContactServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactAPI {
    private final ContactServiceImpl contactService;

    @PostMapping("/{citeid}")
    @PreAuthorize("hasRole('Bailleur')")
    public ResponseEntity<Map<String, Object>> addcontact(@PathVariable String citeid, @RequestBody List<ContactDTO> dtos, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(contactService.addContact(citeid, dtos, authHeader));
    }
}
