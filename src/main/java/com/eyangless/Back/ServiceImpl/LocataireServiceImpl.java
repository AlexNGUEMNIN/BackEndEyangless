package com.eyangless.Back.ServiceImpl;

import com.eyangless.Back.DTO.UserDTO;
import com.eyangless.Back.Entity.Bailleur;
import com.eyangless.Back.Entity.Locataire;
import com.eyangless.Back.Entity.Role;
import com.eyangless.Back.Repository.LocataireRepository;
import com.eyangless.Back.Repository.RoleRepository;
import com.eyangless.Back.Repository.UserRepository;
import com.eyangless.Back.Service.LocataireService;
import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@AllArgsConstructor
public class LocataireServiceImpl implements LocataireService {
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private EmailServiceImpl emailServiceImpl;
    private LocataireRepository locataireRepository;
    private UserServiceImpl userService;

    @Override
    public Locataire save(UserDTO dto) {
        Role role = roleRepository.findRoleByLibelle("Locataire");
        Locataire locataire = new Locataire();
        locataire.setNom(dto.getNom());
        locataire.setPrenom(dto.getPrenom());
        locataire.setEmail(dto.getEmail());
        locataire.setTelephone(dto.getTelephone());
        locataire.setPassword(passwordEncoder.encode(dto.getPassword()));
        locataire.addRole(role);
        locataire.setStatus(false);
        String otp = String.valueOf(new Random().nextInt(999999));
        locataire.setOtp(otp);
        locataire.setOtpExpiry(LocalDateTime.now().plusMinutes(60));
        role.addUtilisateur(locataire);
        role.setId(role.getId());
        roleRepository.save(role);
        try {
            emailServiceImpl.envoieMailhtml(locataire.getEmail(), "code d'activation du compte eyangless", "<!DOCTYPE html>\n" +
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
                    "        <span class=\"otp-code text-primary\">"+locataire.getOtp()+"</span> <!-- Remplace par ton vrai code OTP dynamiquement -->\n" +
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
        return userRepository.save(locataire);
    }

    @Override
    public Locataire update(String id, Locataire locataire) {
        Locataire existing = locataireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Locataire non trouvé"));
        existing.setNom(locataire.getNom());
        existing.setPrenom(locataire.getPrenom());
        existing.setEmail(locataire.getEmail());
        existing.setPassword(locataire.getPassword());
        existing.setStatus(locataire.getStatus());
        return locataireRepository.save(existing);
    }

    @Override
    public Map<String, Object> delete(String id, String autheader) {
        Map<String, Object> response = new HashMap<>();
        Claims claims = userService.extractAllClaims(autheader);
        String id_bailleur = (String) claims.get("id_utilisateur");
        List<Map<String, Object>> roles = (List<Map<String, Object>>) claims.get("roles");
        String role = roles.get(0).get("libelle").toString();
        Locataire locataire = new Locataire();
        try {
            locataire = (Locataire) userRepository.findUserById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (role.compareTo("Locataire") == 0 && locataire.getId().compareTo(id_bailleur)==0) {
            locataireRepository.deleteById(id);
            response.put("message", "supprimer avec success");
            return response;
        }
        if (role.compareTo("Moderateur")==0) {
            locataireRepository.deleteById(id);
            response.put("message", "supprimer avec success");
            return response;
        }
        response.put("message", "NON AUTHORISER");
        return response;
    }

    @Override
    public Locataire getById(String id) {
        return locataireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Locataire non trouvé"));
    }

    @Override
    public List<Locataire> getAll() {
        return locataireRepository.findAll();
    }
}
