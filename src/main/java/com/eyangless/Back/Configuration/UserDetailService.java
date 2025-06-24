package com.eyangless.Back.Configuration;

import com.eyangless.Back.Entity.User;
import com.eyangless.Back.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository utilisateurRepository;

    public UserDetailService(UserRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User utilisateur = utilisateurRepository.findUserByEmail(username);
        if (utilisateur == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return utilisateur;
    }
}
