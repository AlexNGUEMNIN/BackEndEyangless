package com.eyangless.Back.API;

import com.eyangless.Back.Entity.Bailleur;
import com.eyangless.Back.Entity.User;
import com.eyangless.Back.Service.BailleurService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bailleurs")
@RequiredArgsConstructor
public class BailleurAPI {
    private final BailleurService bailleurService;



    @GetMapping
    public List<Bailleur> getAll() {
        return bailleurService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bailleur> get(@PathVariable String id) {
        return bailleurService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public Bailleur getByEmail(@PathVariable String email) {
        return bailleurService.findByEmail(email);
    }

    @GetMapping("/nom")
    public List<Bailleur> search(@RequestParam String search) {
        return bailleurService.searchByNom(search);
    }

//    @PostMapping
//    public ResponseEntity<Bailleur> create(@RequestBody Bailleur b) {
//        return ResponseEntity.ok(bailleurService.save(b));
//    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable String id, @RequestBody User b) {
        return ResponseEntity.ok(bailleurService.updateUser(id, b));
    }

    @PatchMapping("/{id}/suspendre")
    public ResponseEntity<Bailleur> suspend(@PathVariable String id) {
        return ResponseEntity.ok(bailleurService.suspendUser(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Bailleur') or hasRole('Moderateur')")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(bailleurService.delete(id, authHeader));
    }
}
