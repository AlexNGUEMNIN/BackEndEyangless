package com.eyangless.Back.API;

import com.eyangless.Back.DTO.ChambreDTO;
import com.eyangless.Back.Entity.Chambre;
import com.eyangless.Back.Service.ChambreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chambres")
@RequiredArgsConstructor
public class ChambreAPI {

    private final ChambreService chambreService;

    @PostMapping("/cite")
    @PreAuthorize("hasRole('Bailleur')")
    public ResponseEntity<ChambreDTO> createChambre(@RequestBody ChambreDTO chambreDTO, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(chambreService.createChambre(chambreDTO, authHeader));
    }

    @GetMapping("/cite/{citeId}/{pageNo}/{pagesize}")
    public ResponseEntity<List<Chambre>> getChambresByCite(@PathVariable String citeId, @PathVariable int pageNo, @PathVariable int pagesize) {
        return ResponseEntity.ok(chambreService.getChambresByCite(citeId, pageNo, pagesize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chambre> getChambreById(@PathVariable String id) {
        return ResponseEntity.ok(chambreService.getChambreById(id));
    }

    @PutMapping("")
    @PreAuthorize("hasRole('Bailleur')")
    public ResponseEntity<Map<String, Object>> updateChambre(@RequestBody ChambreDTO dto, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(chambreService.updateChambre(dto, authHeader));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Bailleur')")
    public ResponseEntity<Map<String, Object>> deleteChambre(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(chambreService.deleteChambre(id, authHeader));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<Map<String, Object>> changeStatus(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(chambreService.changeStatus(id, authHeader));
    }
}
