package com.eyangless.Back.Repository;

import com.eyangless.Back.Entity.Localisation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalisationRepository extends JpaRepository<Localisation, String> {
    Localisation findLocalisationById(String id);
}
