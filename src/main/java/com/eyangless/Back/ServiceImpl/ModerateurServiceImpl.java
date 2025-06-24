package com.eyangless.Back.ServiceImpl;

import com.eyangless.Back.DTO.UserDTO;
import com.eyangless.Back.Entity.Locataire;
import com.eyangless.Back.Entity.Moderateur;
import com.eyangless.Back.Entity.Role;
import com.eyangless.Back.Repository.ModerateurRepository;
import com.eyangless.Back.Repository.RoleRepository;
import com.eyangless.Back.Repository.UserRepository;
import com.eyangless.Back.Service.ModerateurService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class ModerateurServiceImpl implements ModerateurService {
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private EmailServiceImpl emailServiceImpl;
    private ModerateurRepository moderateurRepository;

    @Override
    public Moderateur save(UserDTO dto) {
        Role role = roleRepository.findRoleByLibelle("Moderateur");
        Moderateur moderateur = new Moderateur();
        moderateur.setNom(dto.getNom());
        moderateur.setPrenom(dto.getPrenom());
        moderateur.setEmail(dto.getEmail());
        moderateur.setTelephone(dto.getTelephone());
        moderateur.setPassword(passwordEncoder.encode(dto.getPassword()));
        moderateur.addRole(role);
        moderateur.setStatus(false);
        String otp = String.valueOf(new Random().nextInt(999999));
        moderateur.setOtp(otp);
        moderateur.setOtpExpiry(LocalDateTime.now().plusMinutes(60));
        role.addUtilisateur(moderateur);
        role.setId(role.getId());
        roleRepository.save(role);
        try {
            emailServiceImpl.envoieMailhtml(moderateur.getEmail(), "code d'activation du compte eyangless", "<!DOCTYPE html>\n" +
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
                    "        <span class=\"otp-code text-primary\">"+moderateur.getOtp()+"</span> <!-- Remplace par ton vrai code OTP dynamiquement -->\n" +
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
        return userRepository.save(moderateur);
    }

    @Override
    public Moderateur update(String id, Moderateur moderateur) {
        Moderateur existing = moderateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modérateur non trouvé"));
        existing.setNom(moderateur.getNom());
        existing.setPrenom(moderateur.getPrenom());
        existing.setEmail(moderateur.getEmail());
        existing.setPassword(moderateur.getPassword());
        existing.setStatus(moderateur.getStatus());
        return moderateurRepository.save(existing);
    }

    @Override
    public void delete(String id) {
        moderateurRepository.deleteById(id);
    }

    @Override
    public Moderateur getById(String id) {
        return moderateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modérateur non trouvé"));
    }

    @Override
    public List<Moderateur> getAll() {
        return moderateurRepository.findAll();
    }
}
