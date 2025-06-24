package com.eyangless.Back.ServiceImpl;

import com.eyangless.Back.DTO.ChambreDTO;
import com.eyangless.Back.Entity.*;
import com.eyangless.Back.Repository.*;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// * Tests unitaires pour ChambreServiceImpl
// *
// * Cette classe teste toutes les méthodes du service ChambreServiceImpl :
// * - createChambre : Création d'une nouvelle chambre
// * - getChambresByCite : Récupération des chambres par cité avec pagination
// * - getChambreById : Récupération d'une chambre par son ID
// * - deleteChambre : Suppression d'une chambre avec vérification des droits
// * - updateChambre : Mise à jour d'une chambre avec vérification des droits
@ExtendWith(MockitoExtension.class)
class ChambreServiceImplTest {
    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private CiteRepository citeRepository;

    @Mock
    private GroupeRepository groupeRepository;

    @Mock
    private BailleurRepository bailleurRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private ChambreServiceImpl chambreService;

    private ChambreDTO chambreDTO;
    private Chambre chambre;
    private Cite cite;
    private Groupe groupe;
    private Bailleur bailleur;
    private Claims claims;
    private String authHeader;

    @BeforeEach
    void setUp() {
        // Initialisation des données de test
        authHeader = "Bearer valid.jwt.token";

        // Mock du bailleur
        bailleur = new Bailleur();
        bailleur.setId("bailleur123");
        bailleur.setNom("Test Bailleur");

        // Mock de la cité
        cite = new Cite();
        cite.setId("cite123");
        cite.setName("Test Cite");
        cite.setChambres(new ArrayList<>());

        // Mock du groupe
        groupe = new Groupe();
        groupe.setId("groupe123");

        // Mock de la chambre
        chambre = new Chambre();
        chambre.setId("chambre123");
        chambre.setCite(cite);
        chambre.setGroupe(groupe);
        chambre.setBailleur(bailleur);

        // Mock du DTO
        chambreDTO = new ChambreDTO();
        chambreDTO.setId("chambre123");
        chambreDTO.setCiteId("cite123");
        chambreDTO.setGroupeId("groupe123");

        // Mock des claims JWT
        claims = mock(Claims.class);
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("libelle", "Bailleur");
        List<Map<String, Object>> roles = Arrays.asList(roleMap);

        when(claims.get("id_utilisateur")).thenReturn("bailleur123");
        when(claims.get("roles")).thenReturn(roles);
    }

    /**
     * Test de création d'une chambre avec succès
     * Vérifie que la chambre est créée correctement quand l'utilisateur est un bailleur
     */
    @Test
    void testCreateChambre_Success() {
        // Given
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(citeRepository.findCiteById("cite123")).thenReturn(cite);
        when(groupeRepository.findGroupeById("groupe123")).thenReturn(groupe);
        when(bailleurRepository.findBailleursById("bailleur123")).thenReturn(bailleur);
        when(chambreRepository.save(any(Chambre.class))).thenReturn(chambre);
        when(citeRepository.save(any(Cite.class))).thenReturn(cite);

        // When
        ChambreDTO result = chambreService.createChambre(chambreDTO, authHeader);

        // Then
        assertNotNull(result);
        assertEquals("chambre123", result.getId());
        assertEquals("cite123", result.getCiteId());
        assertEquals("groupe123", result.getGroupeId());

        verify(chambreRepository).save(any(Chambre.class));
        verify(citeRepository).save(cite);
        assertTrue(cite.getChambres().contains(chambre));
    }

    /**
     * Test de création d'une chambre avec un utilisateur non-bailleur
     * Vérifie que la création échoue si l'utilisateur n'est pas un bailleur
     */
    @Test
    void testCreateChambre_NonBailleurUser() {
        // Given
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("libelle", "Locataire"); // Utilisateur non-bailleur
        List<Map<String, Object>> roles = Arrays.asList(roleMap);
        when(claims.get("roles")).thenReturn(roles);
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);

        // When
        ChambreDTO result = chambreService.createChambre(chambreDTO, authHeader);

        // Then
        assertNull(result);
        verify(chambreRepository, never()).save(any(Chambre.class));
    }

    /**
     * Test de récupération des chambres par cité avec pagination
     * Vérifie que les chambres sont récupérées correctement avec la pagination
     */
    @Test
    void testGetChambresByCite() {
        // Given
        String citeId = "cite123";
        int pageNo = 0;
        int pageSize = 10;
        Pageable expectedPageable = PageRequest.of(pageNo, pageSize);
        List<Chambre> expectedChambres = Arrays.asList(chambre);

        when(chambreRepository.findChambreByCiteId(citeId, expectedPageable)).thenReturn(expectedChambres);

        // When
        List<Chambre> result = chambreService.getChambresByCite(citeId, pageNo, pageSize);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(chambre, result.get(0));
        verify(chambreRepository).findChambreByCiteId(citeId, expectedPageable);
    }

    /**
     * Test de récupération d'une chambre par ID
     * Vérifie que la chambre est récupérée correctement par son ID
     */
    @Test
    void testGetChambreById() {
        // Given
        String chambreId = "chambre123";
        when(chambreRepository.findChambreById(chambreId)).thenReturn(chambre);

        // When
        Chambre result = chambreService.getChambreById(chambreId);

        // Then
        assertNotNull(result);
        assertEquals(chambre, result);
        verify(chambreRepository).findChambreById(chambreId);
    }

    /**
     * Test de suppression d'une chambre avec succès
     * Vérifie que la chambre est supprimée quand l'utilisateur est le propriétaire bailleur
     */
    @Test
    void testDeleteChambre_Success() {
        // Given
        String chambreId = "chambre123";
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(chambreRepository.findChambreById(chambreId)).thenReturn(chambre);

        // When
        Map<String, Object> result = chambreService.deleteChambre(chambreId, authHeader);

        // Then
        assertNotNull(result);
        assertEquals("delete successfully", result.get("status"));
        verify(chambreRepository).delete(chambre);
    }

    /**
     * Test de suppression d'une chambre avec accès refusé (utilisateur non-bailleur)
     * Vérifie que la suppression échoue si l'utilisateur n'est pas un bailleur
     */
    @Test
    void testDeleteChambre_AccessDenied_NonBailleur() {
        // Given
        String chambreId = "chambre123";
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("libelle", "Locataire");
        List<Map<String, Object>> roles = Arrays.asList(roleMap);
        when(claims.get("roles")).thenReturn(roles);
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(chambreRepository.findChambreById(chambreId)).thenReturn(chambre);

        // When
        Map<String, Object> result = chambreService.deleteChambre(chambreId, authHeader);

        // Then
        assertNotNull(result);
        assertEquals("ACCESS DENIED", result.get("status"));
        verify(chambreRepository, never()).delete(any(Chambre.class));
    }

    /**
     * Test de suppression d'une chambre avec accès refusé (bailleur différent)
     * Vérifie que la suppression échoue si le bailleur n'est pas le propriétaire de la chambre
     */
    @Test
    void testDeleteChambre_AccessDenied_DifferentBailleur() {
        // Given
        String chambreId = "chambre123";
        when(claims.get("id_utilisateur")).thenReturn("autre_bailleur456");
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(chambreRepository.findChambreById(chambreId)).thenReturn(chambre);

        // When
        Map<String, Object> result = chambreService.deleteChambre(chambreId, authHeader);

        // Then
        assertNotNull(result);
        assertEquals("ACCESS DENIED", result.get("status"));
        verify(chambreRepository, never()).delete(any(Chambre.class));
    }

    /**
     * Test de mise à jour d'une chambre avec succès
     * Vérifie que la chambre est mise à jour correctement quand l'utilisateur est le propriétaire bailleur
     */
    @Test
    void testUpdateChambre_Success() {
        // Given
        Groupe nouveauGroupe = new Groupe();
        nouveauGroupe.setId("nouveau_groupe456");
        chambreDTO.setGroupeId("nouveau_groupe456");

        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(chambreRepository.findChambreById("chambre123")).thenReturn(chambre);
        when(groupeRepository.findGroupeById("nouveau_groupe456")).thenReturn(nouveauGroupe);
        when(chambreRepository.save(chambre)).thenReturn(chambre);

        // When
        Map<String, Object> result = chambreService.updateChambre(chambreDTO, authHeader);

        // Then
        assertNotNull(result);
        assertEquals("updated successfully", result.get("status"));
        assertEquals(nouveauGroupe, chambre.getGroupe());
        verify(chambreRepository).save(chambre);
    }

    /**
     * Test de mise à jour d'une chambre avec accès refusé
     * Vérifie que la mise à jour échoue si l'utilisateur n'est pas autorisé
     */
    @Test
    void testUpdateChambre_AccessDenied() {
        // Given
        when(claims.get("id_utilisateur")).thenReturn("autre_bailleur456");
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(chambreRepository.findChambreById("chambre123")).thenReturn(chambre);

        // When
        Map<String, Object> result = chambreService.updateChambre(chambreDTO, authHeader);

        // Then
        assertNotNull(result);
        assertEquals("ACCESS DENIED", result.get("status"));
        verify(chambreRepository, never()).save(any(Chambre.class));
    }

    /**
     * Test de mise à jour d'une chambre avec groupe non trouvé
     * Vérifie que la mise à jour échoue si le groupe spécifié n'existe pas
     */
    @Test
    void testUpdateChambre_GroupeNotFound() {
        // Given
        chambreDTO.setGroupeId("groupe_inexistant");
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(chambreRepository.findChambreById("chambre123")).thenReturn(chambre);
        when(groupeRepository.findGroupeById("groupe_inexistant")).thenReturn(null);

        // When
        Map<String, Object> result = chambreService.updateChambre(chambreDTO, authHeader);

        // Then
        assertNotNull(result);
        assertEquals("cannot find groupe", result.get("status"));
        verify(chambreRepository, never()).save(any(Chambre.class));
    }
}
