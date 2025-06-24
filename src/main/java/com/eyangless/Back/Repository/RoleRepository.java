package com.eyangless.Back.Repository;

import com.eyangless.Back.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    public Role findRoleByLibelle(String Libelle);
}
