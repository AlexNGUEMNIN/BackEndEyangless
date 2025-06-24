package com.eyangless.Back.ServiceImpl;

import com.eyangless.Back.DTO.ForgetPasswordRequest;
import com.eyangless.Back.DTO.ResetPasswordRequest;
import com.eyangless.Back.DTO.VerifyOtpRequest;
import com.eyangless.Back.Entity.User;
import com.eyangless.Back.Repository.UserRepository;
import com.eyangless.Back.Service.EmailService;
import com.eyangless.Back.Service.PasswordResetService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {
    private UserRepository userRepository;
    private EmailService emailService;
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> forgotPassword(ForgetPasswordRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Vérifier si l'utilisateur existe
            Optional<User> userOpt = Optional.ofNullable(userRepository.findUserByEmail(request.getEmail()));
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Aucun compte trouvé avec cette adresse email");
                return response;
            }

            User user = userOpt.get();

            // Générer un OTP de 6 chiffres
            String otp = generateOtp();

            // Sauvegarder l'OTP et définir l'expiration (10 minutes)
            user.setOtp(otp);
            user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
            userRepository.save(user);

            // Envoyer l'email avec l'OTP
            emailService.sendOtpEmail(request.getEmail(), otp);

            response.put("success", true);
            response.put("message", "Un code de vérification a été envoyé à votre adresse email");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de l'envoi du code de vérification");
        }

        return response;
    }

    @Override
    public Map<String, Object> verifyOtp(VerifyOtpRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<User> userOpt = Optional.ofNullable(userRepository.findUserByEmail(request.getEmail()));
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Utilisateur non trouvé");
                return response;
            }

            User user = userOpt.get();

            // Vérifier si l'OTP existe
            if (user.getOtp() == null) {
                response.put("success", false);
                response.put("message", "Aucun code de vérification généré");
                return response;
            }

            // Vérifier si l'OTP n'a pas expiré
            if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
                response.put("success", false);
                response.put("message", "Le code de vérification a expiré");
                return response;
            }

            // Vérifier si l'OTP est correct
            if (!user.getOtp().equals(request.getOtp())) {
                response.put("success", false);
                response.put("message", "Code de vérification incorrect");
                return response;
            }

            response.put("success", true);
            response.put("message", "Code de vérification valide");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la vérification du code");
        }

        return response;
    }

    @Override
    public Map<String, Object> resetPassword(ResetPasswordRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Vérifier que les mots de passe correspondent
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                response.put("success", false);
                response.put("message", "Les mots de passe ne correspondent pas");
                return response;
            }

            // Vérifier la force du mot de passe
            if (request.getNewPassword().length() < 8) {
                response.put("success", false);
                response.put("message", "Le mot de passe doit contenir au moins 8 caractères");
                return response;
            }

            Optional<User> userOpt = Optional.ofNullable(userRepository.findUserByEmail(request.getEmail()));
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Utilisateur non trouvé");
                return response;
            }

            User user = userOpt.get();

            // Vérifier à nouveau l'OTP
            if (user.getOtp() == null || !user.getOtp().equals(request.getOtp()) ||
                    user.getOtpExpiry().isBefore(LocalDateTime.now())) {
                response.put("success", false);
                response.put("message", "Code de vérification invalide ou expiré");
                return response;
            }

            // Mettre à jour le mot de passe
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));

            // Nettoyer l'OTP
            user.setOtp(null);
            user.setOtpExpiry(null);

            // Mettre à jour la date de modification
            user.setUpdated_at(new java.util.Date());

            userRepository.save(user);

            response.put("success", true);
            response.put("message", "Mot de passe réinitialisé avec succès");

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors de la réinitialisation du mot de passe");
        }

        return response;
    }
//Fonction pour générer un code OTP
    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // Génère un nombre entre 100000 et 999999
        return String.valueOf(otp);
    }
}
