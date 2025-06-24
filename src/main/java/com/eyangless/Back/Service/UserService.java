package com.eyangless.Back.Service;

import com.eyangless.Back.Entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    public User findByEmail(String email);
    public User save(User user);
    void delete(String id);

    User updateUser(String id, User updatedUser);
    List<User> findAll();

    Optional<User> findById(String id);

    List<User> searchByNom(String nom);

    User suspendUser(String id);
}
