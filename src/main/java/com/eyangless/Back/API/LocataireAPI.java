package com.eyangless.Back.API;

import com.eyangless.Back.Entity.Locataire;
import com.eyangless.Back.Service.LocataireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/locataires")
@RequiredArgsConstructor
public class LocataireAPI {
    private final LocataireService locataireService;
//    @PostMapping
//    public ResponseEntity<Locataire> save(@RequestBody Locataire locataire) {
//        return ResponseEntity.ok(locataireService.save(locataire));
//    }

    @PutMapping("/{id}")
    public ResponseEntity<Locataire> update(@PathVariable String id, @RequestBody Locataire locataire) {
        return ResponseEntity.ok(locataireService.update(id, locataire));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Locataire') or hasRole('Moderateur')")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(locataireService.delete(id, authHeader));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Locataire> getById(@PathVariable String id) {
        return ResponseEntity.ok(locataireService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Locataire>> getAll() {
        return ResponseEntity.ok(locataireService.getAll());
    }
}
