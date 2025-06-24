package com.eyangless.Back.Repository;

import com.eyangless.Back.Entity.Bailleur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BailleurRepository extends JpaRepository<Bailleur, String> {

    public Bailleur findByEmail(String email);

    public Bailleur findByNomContainingIgnoreCase(String nom);

    public Bailleur findBailleursById(String id);
}
