package com.eyangless.Back.API;

import com.eyangless.Back.Entity.Moderateur;
import com.eyangless.Back.Service.ModerateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moderateurs")
@RequiredArgsConstructor
public class ModerateurAPI {
    private final ModerateurService moderateurService;

//    @PostMapping
//    public ResponseEntity<Moderateur> save(@RequestBody Moderateur moderateur) {
//        return ResponseEntity.ok(moderateurService.save(moderateur));
//    }

    @PutMapping("/{id}")
    public ResponseEntity<Moderateur> update(@PathVariable String id, @RequestBody Moderateur moderateur) {
        return ResponseEntity.ok(moderateurService.update(id, moderateur));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        moderateurService.delete(id);
        return ResponseEntity.ok("Modérateur supprimé");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Moderateur> getById(@PathVariable String id) {
        return ResponseEntity.ok(moderateurService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Moderateur>> getAll() {
        return ResponseEntity.ok(moderateurService.getAll());
    }

}
