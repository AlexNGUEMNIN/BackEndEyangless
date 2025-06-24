package com.eyangless.Back.Configuration;

import com.eyangless.Back.Entity.User;
import com.eyangless.Back.ServiceImpl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class ConnexionAPI {
    @Autowired
    private UserServiceImpl utilisateurService;

    @PostMapping("/login")
    public Map<String, Object> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        return utilisateurService.login(authRequest);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request) {
        User user = new User();
        try {
            user = utilisateurService.findByEmail(request.getEmail());
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
        }

        if (!user.getOtp().equals(request.getOtp()) || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("OTP invalide ou expiré");
        }

        user.setStatus(true); // activation du compte
        user.setOtp(null);
        user.setOtpExpiry(null);
        utilisateurService.save(user);

        return ResponseEntity.ok("Compte activé avec succès");
    }


    @PostMapping("/isexpired")
    public Map<String, Object> isExpired(@RequestBody String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
//        log.info(token);
//        log.info(String.valueOf(utilisateurService.isTokenExpired(token)));
        Map<String, Object> response = new HashMap<>();
            if (utilisateurService.isTokenExpired(token) == true) {
                response.put("message", "token toujour valide");
                response.put("success", true);
            }
            if(utilisateurService.isTokenExpired(token) == false) {
                response.put("message", "token non valide");
                response.put("success", false);
            }
            return response;
    }

    @GetMapping("me")
    public User getConnectedUtilisateur(){
        System.out.println("getconnecteduser....");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        log.info(String.valueOf(authentication));
        if(authentication != null && authentication.getPrincipal() instanceof UserDetails){
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            User utilisateurDTO = utilisateurService.findByEmail(username);
            return utilisateurDTO;
        }else {
            System.out.println("Aucun user n'est connecté");
            return null;
        }
    }
}
