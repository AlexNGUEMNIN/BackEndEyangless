package com.eyangless.Back.API;


import com.eyangless.Back.DTO.GroupeDTO;
import com.eyangless.Back.Entity.Groupe;
import com.eyangless.Back.Service.GroupeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/groupes")
@RequiredArgsConstructor
public class GroupeAPI {

    private final GroupeService groupeService;

    @PostMapping
    @PreAuthorize("hasRole('Bailleur')")
    public ResponseEntity<GroupeDTO> createGroupe(@RequestBody GroupeDTO dto, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(groupeService.createGroupe(dto, authHeader));
    }

    @PutMapping("")
    @PreAuthorize("hasRole('Bailleur')")
    public ResponseEntity<Map<String, Object>> updateGroupe(@RequestBody GroupeDTO dto, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(groupeService.updateGroupe(dto, authHeader));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Groupe> getGroupeById(@PathVariable String id) {
        return ResponseEntity.ok(groupeService.getGroupeById(id));
    }

    @GetMapping("/by-cite/{citeId}/{pageNo}/{pagesize}")
    public ResponseEntity<List<Groupe>> getGroupesByCite(@PathVariable String citeId, @PathVariable int pageNo, @PathVariable int pagesize) {
        return ResponseEntity.ok(groupeService.getGroupesByCite(citeId, pageNo, pagesize));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Bailleur')")
    public ResponseEntity<String> deleteGroupe(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(groupeService.deleteGroupe(id, authHeader));
    }
}

