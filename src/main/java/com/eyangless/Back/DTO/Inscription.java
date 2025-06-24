package com.eyangless.Back.DTO;

import com.eyangless.Back.Entity.Role;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Inscription {
    private String nom;
    private String email;
    private String password;
    private Role role;
}
