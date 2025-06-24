package com.eyangless.Back.Configuration;

import com.eyangless.Back.Entity.User;
import com.eyangless.Back.Repository.UserRepository;
import com.eyangless.Back.ServiceImpl.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@Slf4j
//@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private UtilisateurServiceImpl utilisateurService;
    @Autowired
    private UserRepository utilisateurRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        UserServiceImpl utilisateurService = new UserServiceImpl();
        log.info(String.valueOf(request));
        String header = request.getHeader("Authorization");
        log.info(header);
        log.info(request.getServletPath());
        if (header == null || (request.getServletPath().startsWith("/auth") || request.getServletPath().startsWith("/locataire"))) {
            log.info("pas de token et requete d'authentification");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);
            log.info(token);

            if (utilisateurService.isTokenExpired(header)) {
                // Récupérer les informations de l'utilisateur à partir du token
                String username = utilisateurService.extractUsername(header);
                User userDetails = utilisateurRepository.findUserByEmail(username);

                // Créer l'authentification avec les autorités (rôles)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Ajouter des détails de la requête
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Mettre à jour le SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("Authentification réussie pour l'utilisateur: " + username);
                filterChain.doFilter(request, response);
            } else {
                log.info("Token expiré");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (Exception e) {
//            log.error("Erreur d'authentification: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
