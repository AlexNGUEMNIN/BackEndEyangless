package com.eyangless.Back.Repository;

import com.eyangless.Back.Entity.Caracteristique;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaracteristiqueRepository extends JpaRepository<Caracteristique, String> {
    Caracteristique findCaracteristiqueById(String id);
}
