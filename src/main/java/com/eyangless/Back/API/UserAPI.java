package com.eyangless.Back.API;

import com.eyangless.Back.DTO.UserDTO;
import com.eyangless.Back.Entity.Bailleur;
import com.eyangless.Back.Entity.Locataire;
import com.eyangless.Back.Entity.Moderateur;
import com.eyangless.Back.Entity.User;
import com.eyangless.Back.Service.UserService;
import com.eyangless.Back.ServiceImpl.BaileurServiceImpl;
import com.eyangless.Back.ServiceImpl.LocataireServiceImpl;
import com.eyangless.Back.ServiceImpl.ModerateurServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserAPI {
    private LocataireServiceImpl locataireService;
    private BaileurServiceImpl baileurService;
    private ModerateurServiceImpl moderateurService;
    private UserService userService;

    @PostMapping("/locataire")
    public Map<String, Object> savel(@RequestBody UserDTO locataire) {
        log.info("locataire creation");
        Locataire locataire1 = locataireService.save(locataire);
        Map<String, Object> rep = new HashMap<>();
        if (locataire1 == null){
            rep.put("success", false);
            rep.put("message", "Echec de l'enregistrement");
            return rep;
        }
        rep.put("success",true);
        rep.put("message", "Enregistre avec succes");
        return rep;
    }

    @PostMapping("/bailleur")
    public Map<String, Object> saveb(@RequestBody UserDTO bailleur) {
        Bailleur bailleur1 = baileurService.save(bailleur);
        Map<String, Object> rep = new HashMap<>();
        if (bailleur1 == null){
            rep.put("success", false);
            rep.put("message", "Echec de l'enregistrement");
            return rep;
        }
        rep.put("success",true);
        rep.put("message", "Enregistre avec succes");
        return rep;
    }

    @PostMapping("/moderateur")
    public Map<String, Object> savem(@RequestBody UserDTO moderateur) {
        Moderateur moderateur1 = moderateurService.save(moderateur);
        Map<String, Object> rep = new HashMap<>();
        if (moderateur1 == null){
            rep.put("success", false);
            rep.put("message", "Echec de l'enregistrement");
            return rep;
        }
        rep.put("success",true);
        rep.put("message", "Enregistre avec succes");
        return rep;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public User getByEmail(@PathVariable String email) {
        return userService.findByEmail(email);
    }

    @GetMapping("/nom")
    public List<User> searchByNom(@RequestParam String search) {
        return userService.searchByNom(search);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        return ResponseEntity.ok(userService.updateUser(id, updatedUser));
    }

    @PatchMapping("/{id}/suspendre")
    public ResponseEntity<User> suspendUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.suspendUser(id));
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
//        userService.delete(id);
//        return ResponseEntity.ok().build();
//    }
}
