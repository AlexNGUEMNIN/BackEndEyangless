package com.eyangless.Back.ServiceImpl;

import com.eyangless.Back.Configuration.AuthRequest;
import com.eyangless.Back.Entity.User;
import com.eyangless.Back.Repository.UserRepository;
import com.eyangless.Back.Service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.*;

import java.time.Instant;
import java.util.*;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    private final String jwtSecret = "EyanglessSecret";
    private final int jwtExpirationInMs = 1000*60*30;


    public String generateToken(String username) {
        Instant now = Instant.now();
        Instant expiryInstant = now.plusMillis(jwtExpirationInMs);
        Date expiryDate = Date.from(expiryInstant);

        try {
            User utilisateur = userRepository.findUserByEmail(username);
            if (utilisateur == null) {
                throw new IllegalArgumentException("Utilisateur introuvable");
            }

            Date currentDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.SECOND, jwtExpirationInMs);
            Date newDate = calendar.getTime();
//            log.info(String.valueOf(newDate));
            final Map<String, Object> claims = new HashMap<>();
            claims.put("id_utilisateur", utilisateur.getId());
            claims.put("nom", utilisateur.getNom());
            claims.put("prenom", utilisateur.getPrenom());
            claims.put("email", utilisateur.getEmail());
            claims.put("roles", utilisateur.getRoles());
//            claims.put(Claims.EXPIRATION, newDate);
//            claims.put(Claims.SUBJECT, utilisateur.getEmail());

            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(newDate)
                    .addClaims(claims)
                    .signWith(SignatureAlgorithm.HS512, jwtSecret)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du JWT", e);
        }
    }

    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Claims extractAllClaims(String header) {
        String token = header.substring(7);
        try {
            return Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new BadCredentialsException("Le token a expiré", e);
        } catch (JwtException e) {
            throw new BadCredentialsException("Token JWT invalide", e);
        }
    }

    public Boolean isTokenExpired(String token) { //retoune true si le token a expirer
//            log.info(token);
//            log.info(String.valueOf(extractAllClaims(token)));
//            log.info(String.valueOf(extractAllClaims(token).getExpiration()));
//            log.info(String.valueOf(extractAllClaims(token).getIssuedAt()));
        return !extractAllClaims(token).getExpiration().before(extractAllClaims(token).getIssuedAt());
    }

    public Map<String, Object> login(AuthRequest authRequest) {
        log.info("connexion de l'utilisateur");
        log.info(authRequest.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            Map<String, Object> response = new HashMap<>();
            log.info(String.valueOf(authentication));
            if (authentication!=null) {
                User utilisateurDTO = userRepository.findUserByEmail(authRequest.getUsername());
                utilisateurDTO.setLogin_at(new Date());
                userRepository.save(utilisateurDTO);
                SecurityContextHolder.getContext().setAuthentication(authentication);
//                log.info(String.valueOf(SecurityContextHolder.getContext().getAuthentication()));
                response.put("role", utilisateurDTO.getRoles());
                String token = this.generateToken(authRequest.getUsername());
//                String username = this.extractUsername(token);
                String username = authRequest.getUsername();
//                log.info("good");
                response.put("token", token);
                response.put("username", username);
                response.put("utilisateur connectee:", SecurityContextHolder.getContext().getAuthentication());
            }
            return response;

        } catch (Exception e) {
            log.info("erreur");
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.UNAUTHORIZED);
            e.printStackTrace();
            return response;
        }
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public User updateUser(String id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setNom(updatedUser.getNom());
                    user.setPrenom(updatedUser.getPrenom());
                    user.setEmail(updatedUser.getEmail());
                    user.setStatus(updatedUser.getStatus());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> searchByNom(String nom) {
        return (List<User>) userRepository.findByNomContainingIgnoreCase(nom);
    }

    @Override
    public User suspendUser(String id) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setStatus(false);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}
