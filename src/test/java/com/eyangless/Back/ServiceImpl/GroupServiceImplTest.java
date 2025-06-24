package com.eyangless.Back.ServiceImpl;
import com.eyangless.Back.DTO.GroupeDTO;
import com.eyangless.Back.Entity.*;
import com.eyangless.Back.Repository.*;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/*
 * Tests unitaires pour GroupeServiceImpl
 *
 * Cette classe teste toutes les méthodes du service GroupeServiceImpl :
 * - createGroupe : Création d'un nouveau groupe avec caractéristiques et images
 * - updateGroupe : Mise à jour d'un groupe avec vérification des droits d'accès
 * - getGroupeById : Récupération d'un groupe par son ID
 * - getGroupesByCite : Récupération des groupes d'une cité avec pagination
 * - deleteGroupe : Suppression d'un groupe avec suppression des chambres associées
 */
@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {
    @Mock
    private GroupeRepository groupeRepository;

    @Mock
    private CiteRepository citeRepository;

    @Mock
    private BailleurRepository bailleurRepository;

    @Mock
    private CaracteristiqueRepository caracteristiqueRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ChambreRepository chambreRepository;

    @InjectMocks
    private GroupeServiceImpl groupeService;

    private GroupeDTO groupeDTO;
    private Groupe groupe;
    private Cite cite;
    private Bailleur bailleur;
    private Claims claims;
    private String authHeader;
    private List<Caracteristique> caracteristiques;
    private List<File> pictures;

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
        cite.setGroupes(new ArrayList<>());

        // Mock des caractéristiques
        Caracteristique caracteristique = new Caracteristique();
        caracteristique.setId("carac123");
        caracteristique.setName("WiFi");
        caracteristique.setDescription("Connexion WiFi gratuite");
        caracteristique.setValue("Disponible");
        caracteristique.setCreated_at(new Date());
        caracteristiques = Arrays.asList(caracteristique);

        // Mock des fichiers/images
        File picture = new File();
        pictures = Arrays.asList(picture);

        // Mock du groupe
        groupe = new Groupe();
        groupe.setId("groupe123");
        groupe.setSuperficie(25);
        groupe.setCite(cite);
        groupe.setBailleur(bailleur);
        groupe.setCaracteristiques(caracteristiques);
        groupe.setPictures(pictures);

        // Mock du DTO
        groupeDTO = new GroupeDTO();
        groupeDTO.setId("groupe123");
        groupeDTO.setSuperficie(25);
        groupeDTO.setCiteId("cite123");
        groupeDTO.setCaracteristiques(caracteristiques);
        groupeDTO.setPicturePaths(pictures);

        // Mock des claims JWT
        claims = mock(Claims.class);
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("libelle", "Bailleur");
        List<Map<String, Object>> roles = Arrays.asList(roleMap);

        when(claims.get("id_utilisateur")).thenReturn("bailleur123");
        when(claims.get("roles")).thenReturn(roles);
    }

    /**
     * Test de création d'un groupe avec succès
     * Vérifie que le groupe est créé correctement avec tous ses éléments associés
     */
    @Test
    void testCreateGroupe_Success() {
        // Given
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(citeRepository.findCiteById("cite123")).thenReturn(cite);
        when(bailleurRepository.findBailleursById("bailleur123")).thenReturn(bailleur);
        when(groupeRepository.save(any(Groupe.class))).thenReturn(groupe);
        when(citeRepository.save(any(Cite.class))).thenReturn(cite);
        when(caracteristiqueRepository.save(any(Caracteristique.class))).thenReturn(caracteristiques.get(0));

        // When
        GroupeDTO result = groupeService.createGroupe(groupeDTO, authHeader);

        // Then
        assertNotNull(result);
        assertEquals("groupe123", result.getId());
        assertEquals(25, result.getSuperficie());
        assertEquals("cite123", result.getCiteId());

        verify(groupeRepository, times(2)).save(any(Groupe.class));
        verify(citeRepository).save(cite);
        verify(caracteristiqueRepository).save(any(Caracteristique.class));
    }

    /**
     * Test de mise à jour d'un groupe avec succès
     * Vérifie que le groupe est mis à jour correctement quand l'utilisateur est le propriétaire
     */
    @Test
    void testUpdateGroupe_Success() {
        // Given
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(groupeRepository.findById("groupe123")).thenReturn(Optional.of(groupe));
        when(caracteristiqueRepository.save(any(Caracteristique.class))).thenReturn(caracteristiques.get(0));
        when(groupeRepository.save(any(Groupe.class))).thenReturn(groupe);

        // When
        Map<String, Object> result = groupeService.updateGroupe(groupeDTO, authHeader);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("success"));
        assertFalse(result.containsKey("status"));

        verify(groupeRepository).save(groupe);
        verify(caracteristiqueRepository).save(any(Caracteristique.class));
    }

    /**
     * Test de mise à jour d'un groupe avec accès refusé (utilisateur non-bailleur)
     * Vérifie que la mise à jour échoue si l'utilisateur n'est pas un bailleur
     */
    @Test
    void testUpdateGroupe_AccessDenied_NonBailleur() {
        // Given
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("libelle", "Locataire");
        List<Map<String, Object>> roles = Arrays.asList(roleMap);
        when(claims.get("roles")).thenReturn(roles);
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(groupeRepository.findById("groupe123")).thenReturn(Optional.of(groupe));

        // When
        Map<String, Object> result = groupeService.updateGroupe(groupeDTO, authHeader);

        // Then
        assertNotNull(result);
        assertEquals("ACCESS DENIED", result.get("status"));
        verify(groupeRepository, never()).save(any(Groupe.class));
    }

    /**
     * Test de mise à jour d'un groupe avec accès refusé (bailleur différent)
     * Vérifie que la mise à jour échoue si le bailleur n'est pas le propriétaire du groupe
     */
    @Test
    void testUpdateGroupe_AccessDenied_DifferentBailleur() {
        // Given
        when(claims.get("id_utilisateur")).thenReturn("autre_bailleur456");
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(groupeRepository.findById("groupe123")).thenReturn(Optional.of(groupe));

        // When
        Map<String, Object> result = groupeService.updateGroupe(groupeDTO, authHeader);

        // Then
        assertNotNull(result);
        assertEquals("ACCESS DENIED", result.get("status"));
        verify(groupeRepository, never()).save(any(Groupe.class));
    }

    /**
     * Test de mise à jour avec caractéristique non autorisée
     * Vérifie que la mise à jour échoue si on essaie de modifier une caractéristique qui n'appartient pas au groupe
     */
    @Test
    void testUpdateGroupe_AccessDenied_UnauthorizedCaracteristique() {
        // Given
        Caracteristique caracNonAutorisee = new Caracteristique();
        caracNonAutorisee.setId("carac_non_autorisee");
        caracNonAutorisee.setName("Non autorisée");

        GroupeDTO dtoAvecCaracNonAutorisee = new GroupeDTO();
        dtoAvecCaracNonAutorisee.setId("groupe123");
        dtoAvecCaracNonAutorisee.setSuperficie(25);
        dtoAvecCaracNonAutorisee.setCaracteristiques(Arrays.asList(caracNonAutorisee));

        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(groupeRepository.findById("groupe123")).thenReturn(Optional.of(groupe));

        // When
        Map<String, Object> result = groupeService.updateGroupe(dtoAvecCaracNonAutorisee, authHeader);

        // Then
        assertNotNull(result);
        assertEquals("ACCESS DENIED", result.get("status"));
        verify(groupeRepository, never()).save(any(Groupe.class));
    }

    /**
     * Test de récupération d'un groupe par ID avec succès
     * Vérifie que le groupe est récupéré correctement par son ID
     */
    @Test
    void testGetGroupeById_Success() {
        // Given
        when(groupeRepository.findById("groupe123")).thenReturn(Optional.of(groupe));

        // When
        Groupe result = groupeService.getGroupeById("groupe123");

        // Then
        assertNotNull(result);
        assertEquals(groupe, result);
        verify(groupeRepository).findById("groupe123");
    }

    /**
     * Test de récupération d'un groupe par ID avec groupe non trouvé
     * Vérifie qu'une exception est levée si le groupe n'existe pas
     */
    @Test
    void testGetGroupeById_NotFound() {
        // Given
        when(groupeRepository.findById("groupe_inexistant")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            groupeService.getGroupeById("groupe_inexistant");
        });

        verify(groupeRepository).findById("groupe_inexistant");
    }

    /**
     * Test de récupération des groupes d'une cité avec pagination
     * Vérifie que les groupes sont récupérés correctement avec pagination
     */
    @Test
    void testGetGroupesByCite() {
        // Given
        int pageNo = 0;
        int pageSize = 10;
        Pageable expectedPageable = PageRequest.of(pageNo, pageSize);
        List<Groupe> groupes = Arrays.asList(groupe);
        Page<Groupe> expectedPage = new PageImpl<>(groupes, expectedPageable, 1);

        when(groupeRepository.findByCiteId(eq("cite123"), any(Pageable.class))).thenReturn((List<Groupe>) expectedPage);

        // When
        List<Groupe> result = groupeService.getGroupesByCite("cite123", pageNo, pageSize);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(groupe, result.get(0));
        verify(groupeRepository).findByCiteId(eq("cite123"), any(Pageable.class));
    }

    /**
     * Test de suppression d'un groupe avec succès
     * Vérifie que le groupe et ses chambres associées sont supprimés
     */
    @Test
    void testDeleteGroupe_Success() {
        // Given
        Chambre chambre = new Chambre();
        chambre.setId("chambre123");
        List<Chambre> chambres = Arrays.asList(chambre);

        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(groupeRepository.findGroupeById("groupe123")).thenReturn(groupe);
        when(chambreRepository.findChambreByGroupe(groupe)).thenReturn(chambres);
        doNothing().when(chambreRepository).delete(any(Chambre.class));
        doNothing().when(groupeRepository).deleteById("groupe123");

        // When
        String result = groupeService.deleteGroupe("groupe123", authHeader);

        // Then
        assertEquals("DELETE SUCCESSFULL", result);
        verify(chambreRepository).delete(chambre);
        verify(groupeRepository).deleteById("groupe123");
    }

    /**
     * Test de suppression d'un groupe avec accès refusé (groupe inexistant)
     * Vérifie que la suppression échoue si le groupe n'existe pas
     */
    @Test
    void testDeleteGroupe_GroupeNotFound() {
        // Given
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(groupeRepository.findGroupeById("groupe123")).thenReturn(null);

        // When
        String result = groupeService.deleteGroupe("groupe123", authHeader);

        // Then
        assertEquals("ACCESS DENIED", result);
        verify(groupeRepository, never()).deleteById(anyString());
    }

    /**
     * Test de suppression d'un groupe avec accès refusé (utilisateur non-bailleur)
     * Vérifie que la suppression échoue si l'utilisateur n'est pas un bailleur
     */
    @Test
    void testDeleteGroupe_AccessDenied_NonBailleur() {
        // Given
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("libelle", "Locataire");
        List<Map<String, Object>> roles = Arrays.asList(roleMap);
        when(claims.get("roles")).thenReturn(roles);
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(groupeRepository.findGroupeById("groupe123")).thenReturn(groupe);

        // When
        String result = groupeService.deleteGroupe("groupe123", authHeader);

        // Then
        assertEquals("ACCESS DENIED", result);
        verify(groupeRepository, never()).deleteById(anyString());
    }

    /**
     * Test de suppression d'un groupe avec accès refusé (bailleur différent)
     * Vérifie que la suppression échoue si le bailleur n'est pas le propriétaire du groupe
     */
    @Test
    void testDeleteGroupe_AccessDenied_DifferentBailleur() {
        // Given
        when(claims.get("id_utilisateur")).thenReturn("autre_bailleur456");
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(groupeRepository.findGroupeById("groupe123")).thenReturn(groupe);

        // When
        String result = groupeService.deleteGroupe("groupe123", authHeader);

        // Then
        assertEquals("ACCESS DENIED", result);
        verify(groupeRepository, never()).deleteById(anyString());
    }

    /**
     * Test de suppression d'un groupe sans chambres associées
     * Vérifie que la suppression fonctionne même s'il n'y a pas de chambres
     */
    @Test
    void testDeleteGroupe_NoChambres() {
        // Given
        List<Chambre> chambresVides = new ArrayList<>();

        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(groupeRepository.findGroupeById("groupe123")).thenReturn(groupe);
        when(chambreRepository.findChambreByGroupe(groupe)).thenReturn(chambresVides);
        doNothing().when(groupeRepository).deleteById("groupe123");

        // When
        String result = groupeService.deleteGroupe("groupe123", authHeader);

        // Then
        assertEquals("DELETE SUCCESSFULL", result);
        verify(chambreRepository, never()).delete(any(Chambre.class));
        verify(groupeRepository).deleteById("groupe123");
    }
}
