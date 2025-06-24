package com.eyangless.Back.ServiceImpl;

import com.eyangless.Back.DTO.CiteDTO;
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
 * Tests unitaires pour CiteServiceImpl
 *
 * Cette classe teste toutes les méthodes du service CiteServiceImpl :
 * - createCite : Création d'une nouvelle cité avec localisation, contacts et caractéristiques
 * - updateCite : Mise à jour d'une cité avec vérification des droits d'accès
 * - getCiteById : Récupération d'une cité par son ID
 * - getAllCites : Récupération de toutes les cités avec pagination
 * - deleteCite : Suppression d'une cité avec vérification des droits
 *
 */
@ExtendWith(MockitoExtension.class)
class CiteServiceImplTest {
    @Mock
    private CiteRepository citeRepository;

    @Mock
    private LocalisationRepository localisationRepository;

    @Mock
    private BailleurRepository bailleurRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private CaracteristiqueRepository caracteristiqueRepository;

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private CiteServiceImpl citeService;

    private CiteDTO citeDTO;
    private Cite cite;
    private Bailleur bailleur;
    private Localisation localisation;
    private Claims claims;
    private String authHeader;
    private List<Contact> contacts;
    private List<Caracteristique> caracteristiques;

    @BeforeEach
    void setUp() {
        // Initialisation des données de test
        authHeader = "Bearer valid.jwt.token";

        // Mock du bailleur
        bailleur = new Bailleur();
        bailleur.setId("bailleur123");
        bailleur.setNom("Test Bailleur");

        // Mock de la localisation - Using correct field names
        localisation = new Localisation();
        localisation.setId("localisation123");
        localisation.setQuartier("Test Quartier");
        localisation.setVille("Test Ville");
        localisation.setLatitude(45.7640);
        localisation.setLongitude(4.8357);

        // Mock des contacts - Using correct field names
        Contact contact = new Contact();
        contact.setId("contact123");
        contact.setName("Contact Test");
        contact.setPhone("123456789");
        contact.setEmail("test@example.com");
        contacts = Arrays.asList(contact);

        // Mock des caractéristiques - Using correct field names
        Caracteristique caracteristique = new Caracteristique();
        caracteristique.setId("carac123");
        caracteristique.setName("WiFi");
        caracteristique.setDescription("Connexion WiFi gratuite");
        caracteristique.setValue("Disponible");
        caracteristiques = Arrays.asList(caracteristique);

        // Mock de la cité
        cite = new Cite();
        cite.setId("cite123");
        cite.setName("Test Cite");
        cite.setDescription("Description test");
        cite.setBailleur(bailleur);
        cite.setLocalisation(localisation);
        cite.setContacts(contacts);
        cite.setSuplements(caracteristiques);

        // Mock du DTO
        citeDTO = new CiteDTO();
        citeDTO.setId("cite123");
        citeDTO.setName("Test Cite");
        citeDTO.setDescription("Description test");
        citeDTO.setLocalisation(localisation);
        citeDTO.setContactList(contacts);
        citeDTO.setSuplementList(caracteristiques);

        // Mock des claims JWT
        claims = mock(Claims.class);
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("libelle", "Bailleur");
        List<Map<String, Object>> roles = Arrays.asList(roleMap);

        when(claims.get("id_utilisateur")).thenReturn("bailleur123");
        when(claims.get("roles")).thenReturn(roles);
    }

    /**
     * Test de création d'une cité avec succès
     * Vérifie que la cité est créée correctement avec tous ses éléments associés
     */
    @Test
    void testCreateCite_Success() {
        // Given
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(bailleurRepository.findBailleursById("bailleur123")).thenReturn(bailleur);
        when(localisationRepository.save(any(Localisation.class))).thenReturn(localisation);
        when(citeRepository.save(any(Cite.class))).thenReturn(cite);
        when(contactRepository.save(any(Contact.class))).thenReturn(contacts.get(0));
        when(caracteristiqueRepository.save(any(Caracteristique.class))).thenReturn(caracteristiques.get(0));

        // When
        CiteDTO result = citeService.createCite(citeDTO, authHeader);

        // Then
        assertNotNull(result);
        assertEquals("cite123", result.getId());
        assertEquals("Test Cite", result.getName());
        assertEquals("Description test", result.getDescription());

        verify(citeRepository, times(2)).save(any(Cite.class));
        verify(localisationRepository).save(localisation);
        verify(contactRepository).save(any(Contact.class));
        verify(caracteristiqueRepository).save(any(Caracteristique.class));
    }

    /**
     * Test de mise à jour d'une cité avec succès
     * Vérifie que la cité est mise à jour correctement quand l'utilisateur est le propriétaire
     */
    @Test
    void testUpdateCite_Success() {
        // Given
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(citeRepository.findCiteById("cite123")).thenReturn(cite);
        when(localisationRepository.findLocalisationById("localisation123")).thenReturn(localisation);
        when(contactRepository.findContactById("contact123")).thenReturn(contacts.get(0));
        when(caracteristiqueRepository.findCaracteristiqueById("carac123")).thenReturn(caracteristiques.get(0));
        when(citeRepository.save(any(Cite.class))).thenReturn(cite);

        // When
        CiteDTO result = citeService.updateCite(citeDTO, authHeader);

        // Then
        assertNotNull(result);
        assertEquals("cite123", result.getId());
        assertEquals("Test Cite", result.getName());
        assertEquals("Description test", result.getDescription());

        verify(citeRepository).save(cite);
    }

    /**
     * Test de mise à jour d'une cité avec accès refusé (cité inexistante)
     * Vérifie que la mise à jour échoue si la cité n'existe pas
     */
    @Test
    void testUpdateCite_CiteNotFound() {
        // Given
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(citeRepository.findCiteById("cite123")).thenReturn(null);

        // When
        CiteDTO result = citeService.updateCite(citeDTO, authHeader);

        // Then
        assertNull(result);
        verify(citeRepository, never()).save(any(Cite.class));
    }

    /**
     * Test de mise à jour d'une cité avec accès refusé (utilisateur non-bailleur)
     * Vérifie que la mise à jour échoue si l'utilisateur n'est pas un bailleur
     */
    @Test
    void testUpdateCite_AccessDenied_NonBailleur() {
        // Given
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("libelle", "Locataire");
        List<Map<String, Object>> roles = Arrays.asList(roleMap);
        when(claims.get("roles")).thenReturn(roles);
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(citeRepository.findCiteById("cite123")).thenReturn(cite);

        // When
        CiteDTO result = citeService.updateCite(citeDTO, authHeader);

        // Then
        assertNull(result);
        verify(citeRepository, never()).save(any(Cite.class));
    }

    /**
     * Test de mise à jour d'une cité avec accès refusé (bailleur différent)
     * Vérifie que la mise à jour échoue si le bailleur n'est pas le propriétaire de la cité
     */
    @Test
    void testUpdateCite_AccessDenied_DifferentBailleur() {
        // Given
        when(claims.get("id_utilisateur")).thenReturn("autre_bailleur456");
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(citeRepository.findCiteById("cite123")).thenReturn(cite);

        // When
        CiteDTO result = citeService.updateCite(citeDTO, authHeader);

        // Then
        assertNull(result);
        verify(citeRepository, never()).save(any(Cite.class));
    }

    /**
     * Test de mise à jour avec création de nouvelle localisation
     * Vérifie qu'une nouvelle localisation est créée si elle n'existe pas
     */
    @Test
    void testUpdateCite_CreateNewLocalisation() {
        // Given
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(citeRepository.findCiteById("cite123")).thenReturn(cite);
        when(localisationRepository.findLocalisationById("localisation123")).thenReturn(null);
        when(localisationRepository.save(any(Localisation.class))).thenReturn(localisation);
        when(contactRepository.findContactById("contact123")).thenReturn(contacts.get(0));
        when(caracteristiqueRepository.findCaracteristiqueById("carac123")).thenReturn(caracteristiques.get(0));
        when(citeRepository.save(any(Cite.class))).thenReturn(cite);

        // When
        CiteDTO result = citeService.updateCite(citeDTO, authHeader);

        // Then
        assertNotNull(result);
        verify(localisationRepository).save(localisation);
        verify(citeRepository).save(cite);
    }

    /**
     * Test de récupération d'une cité par ID avec succès
     * Vérifie que la cité est récupérée correctement par son ID
     */
    @Test
    void testGetCiteById_Success() {
        // Given
        when(citeRepository.findById("cite123")).thenReturn(Optional.of(cite));

        // When
        Cite result = citeService.getCiteById("cite123");

        // Then
        assertNotNull(result);
        assertEquals(cite, result);
        verify(citeRepository).findById("cite123");
    }

    /**
     * Test de récupération d'une cité par ID avec cité non trouvée
     * Vérifie qu'une exception est levée si la cité n'existe pas
     */
    @Test
    void testGetCiteById_NotFound() {
        // Given
        when(citeRepository.findById("cite_inexistante")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            citeService.getCiteById("cite_inexistante");
        });

        assertEquals("Cite not found", exception.getMessage());
        verify(citeRepository).findById("cite_inexistante");
    }

    /**
     * Test de récupération de toutes les cités avec pagination
     * Vérifie que les cités sont récupérées correctement avec pagination
     */
    @Test
    void testGetAllCites() {
        // Given
        int pageNo = 0;
        int pageSize = 10;
        Pageable expectedPageable = PageRequest.of(pageNo, pageSize);
        List<Cite> cites = Arrays.asList(cite);
        Page<Cite> expectedPage = new PageImpl<>(cites, expectedPageable, 1);

        when(citeRepository.findAll(expectedPageable)).thenReturn(expectedPage);

        // When
        Page<Cite> result = citeService.getAllCites(pageNo, pageSize);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(cite, result.getContent().get(0));
        verify(citeRepository).findAll(expectedPageable);
    }

    /**
     * Test de suppression d'une cité avec succès
     * Vérifie que la cité est supprimée quand l'utilisateur est le propriétaire bailleur
     */
    @Test
    void testDeleteCite_Success() {
        // Given
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(citeRepository.findCiteById("cite123")).thenReturn(cite);
        doNothing().when(citeRepository).deleteById("cite123");

        // When
        String result = citeService.deleteCite("cite123", authHeader);

        // Then
        assertEquals("DELETE SUCCESSFULL", result);
        verify(citeRepository).deleteById("cite123");
    }

    /**
     * Test de suppression d'une cité avec accès refusé (cité inexistante)
     * Vérifie que la suppression échoue si la cité n'existe pas
     */
    @Test
    void testDeleteCite_CiteNotFound() {
        // Given
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(citeRepository.findCiteById("cite123")).thenReturn(null);

        // When
        String result = citeService.deleteCite("cite123", authHeader);

        // Then
        assertEquals("ACCESS DENIED", result);
        verify(citeRepository, never()).deleteById(anyString());
    }

    /**
     * Test de suppression d'une cité avec accès refusé (utilisateur non-bailleur)
     * Vérifie que la suppression échoue si l'utilisateur n'est pas un bailleur
     */
    @Test
    void testDeleteCite_AccessDenied_NonBailleur() {
        // Given
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("libelle", "Locataire");
        List<Map<String, Object>> roles = Arrays.asList(roleMap);
        when(claims.get("roles")).thenReturn(roles);
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(citeRepository.findCiteById("cite123")).thenReturn(cite);

        // When
        String result = citeService.deleteCite("cite123", authHeader);

        // Then
        assertEquals("ACCESS DENIED", result);
        verify(citeRepository, never()).deleteById(anyString());
    }

    /**
     * Test de suppression d'une cité avec accès refusé (bailleur différent)
     * Vérifie que la suppression échoue si le bailleur n'est pas le propriétaire de la cité
     */
    @Test
    void testDeleteCite_AccessDenied_DifferentBailleur() {
        // Given
        when(claims.get("id_utilisateur")).thenReturn("autre_bailleur456");
        when(userService.extractAllClaims(authHeader)).thenReturn(claims);
        when(citeRepository.findCiteById("cite123")).thenReturn(cite);

        // When
        String result = citeService.deleteCite("cite123", authHeader);

        // Then
        assertEquals("ACCESS DENIED", result);
        verify(citeRepository, never()).deleteById(anyString());
    }
}
