package com.eyangless.Back.API;



import com.eyangless.Back.DTO.CiteDTO;
import com.eyangless.Back.Entity.Cite;
import com.eyangless.Back.Service.CiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/cites")
@CrossOrigin(origins = "http://localhost:8100") // <-- Ajoute ceci pour autoriser le frontend
@RequiredArgsConstructor
public class CiteAPI {

    private final CiteService citeService;

    @PostMapping
    @PreAuthorize("hasRole('Bailleur')")
    public ResponseEntity<CiteDTO> createCite(@RequestBody CiteDTO dto, @RequestHeader("Authorization") String authHeader) {
        log.info("test");
        return ResponseEntity.ok(citeService.createCite(dto, authHeader));
    }

    @GetMapping("/{pageNo}/{pagesize}")
    public ResponseEntity<Page<Cite>> getAllCites(@PathVariable int pageNo, @PathVariable int pagesize) {
        return ResponseEntity.ok(citeService.getAllCites(pageNo, pagesize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cite> getCiteById(@PathVariable String id) {
        return ResponseEntity.ok(citeService.getCiteById(id));
    }

    @PutMapping("")
    @PreAuthorize("hasRole('Bailleur')")
    public ResponseEntity<CiteDTO> updateCite(@RequestBody CiteDTO dto, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(citeService.updateCite(dto, authHeader));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Bailleur')")
    public ResponseEntity<String> deleteCite(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
//        citeService.deleteCite(id, authHeader);
//        return ResponseEntity.noContent().build();
        return ResponseEntity.ok(citeService.deleteCite(id, authHeader));
    }
}
