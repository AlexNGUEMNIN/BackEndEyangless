package com.eyangless.Back.Repository;


import com.eyangless.Back.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    public User findUserByEmail(String email);
    public User findByNomContainingIgnoreCase(String nom);
    User findUserById(String id);
}
