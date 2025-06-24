package com.eyangless.Back.ServiceImpl;

import com.eyangless.Back.DTO.UserDTO;
import com.eyangless.Back.Entity.Bailleur;
import com.eyangless.Back.Entity.Chambre;
import com.eyangless.Back.Entity.Role;
import com.eyangless.Back.Entity.User;
import com.eyangless.Back.Repository.BailleurRepository;
import com.eyangless.Back.Repository.RoleRepository;
import com.eyangless.Back.Repository.UserRepository;
import com.eyangless.Back.Service.BailleurService;
import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class BaileurServiceImpl implements BailleurService {
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private EmailServiceImpl emailServiceImpl;
    private BailleurRepository bailleurRepository;
    private UserServiceImpl userService;

    @Override
    public Bailleur findByEmail(String email) {
        return bailleurRepository.findByEmail(email);
    }

    @Override
    public Bailleur save(UserDTO dto) {
        Role role = roleRepository.findRoleByLibelle("Bailleur");
        Bailleur bailleur = new Bailleur();
        bailleur.setNom(dto.getNom());
        bailleur.setPrenom(dto.getPrenom());
        bailleur.setEmail(dto.getEmail());
        bailleur.setTelephone(dto.getTelephone());
        bailleur.setTitrefoncier(dto.getTitrefoncier());
        bailleur.setCni(dto.getCni());
        bailleur.setPassword(passwordEncoder.encode(dto.getPassword()));
        bailleur.addRole(role);
        bailleur.setStatus(false);
        String otp = String.valueOf(new Random().nextInt(999999));
        bailleur.setOtp(otp);
        bailleur.setOtpExpiry(LocalDateTime.now().plusMinutes(60));
        role.addUtilisateur(bailleur);
        role.setId(role.getId());
        roleRepository.save(role);
        try {
            emailServiceImpl.envoieMailhtml(bailleur.getEmail(), "code d'activation du compte eyangless", "<!DOCTYPE html>\n" +
                    "<html lang=\"fr\">\n" +
                    "<head>\n" +
                    "  <meta charset=\"UTF-8\">\n" +
                    "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "  <title>Vérification du Code OTP</title>\n" +
                    "  <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
                    "  <style>\n" +
                    "    .otp-code {\n" +
                    "      font-size: 2.5rem;\n" +
                    "      font-weight: bold;\n" +
                    "      letter-spacing: 0.3rem;\n" +
                    "    }\n" +
                    "  </style>\n" +
                    "</head>\n" +
                    "<body class=\"bg-light d-flex justify-content-center align-items-center vh-100\">\n" +
                    "\n" +
                    "  <div class=\"container text-center\">\n" +
                    "    <div class=\"card shadow-lg p-4\">\n" +
                    "      <h2 class=\"mb-3\">Bienvenue sur Eyangless!</h2>\n" +
                    "      <p class=\"lead\">Merci pour votre inscription. Pour finaliser votre enregistrement, veuillez saisir le code OTP ci-dessous dans l’application.</p>\n" +
                    "      \n" +
                    "      <div class=\"alert alert-primary mt-4\">\n" +
                    "        <span class=\"otp-code text-primary\">"+bailleur.getOtp()+"</span> <!-- Remplace par ton vrai code OTP dynamiquement -->\n" +
                    "      </div>\n" +
                    "\n" +
                    "      <p class=\"mt-3 text-muted\">Ce code est valable pendant <strong>60 minutes</strong>. Ne le partagez avec personne.</p>\n" +
                    "    </div>\n" +
                    "  </div>\n" +
                    "\n" +
                    "  <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js\"></script>\n" +
                    "</body>\n" +
                    "</html>\n");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return userRepository.save(bailleur);
    }

    @Override
    public Map<String, Object> delete(String id, String autheader) {
        log.info("suppression du bailleur");
        Map<String, Object> response = new HashMap<>();
        Claims claims = userService.extractAllClaims(autheader);
        String id_bailleur = (String) claims.get("id_utilisateur");
        List<Map<String, Object>> roles = (List<Map<String, Object>>) claims.get("roles");
        String role = roles.get(0).get("libelle").toString();
        Bailleur bailleur = new Bailleur();
        try {
            bailleur = (Bailleur) userRepository.findUserById(id);
            log.info("bailleur: "+bailleur.getNom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (role.compareTo("Bailleur") == 0 && bailleur.getId().compareTo(id_bailleur)==0) {
            bailleurRepository.deleteById(id);
            response.put("message", "supprimer avec success");
            return response;
        }
        if (role.compareTo("Moderateur")==0) {
            bailleurRepository.deleteById(id);
            response.put("message", "supprimer avec success");
            return response;
        }
        response.put("message", "NON AUTHORISER");
        return response;
    }

    @Override
    public User updateUser(String id, User updatedUser) {
        return null;
    }

    @Override
    public List<Bailleur> findAll() {
        return null;
    }

    @Override
    public Optional<Bailleur> findById(String id) {
        return Optional.empty();
    }

    @Override
    public List<Bailleur> searchByNom(String nom) {
        return null;
    }

    @Override
    public Bailleur suspendUser(String id) {
        Bailleur b = bailleurRepository.findById(id).orElseThrow();
        b.setStatus(false);
        return bailleurRepository.save(b);
    }
}
