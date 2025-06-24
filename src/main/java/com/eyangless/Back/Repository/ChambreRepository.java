package com.eyangless.Back.Repository;


import com.eyangless.Back.Entity.Chambre;
import com.eyangless.Back.Entity.Groupe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChambreRepository extends JpaRepository<Chambre, String> {
    List<Chambre> findChambreByCiteId(String citeId, Pageable pageable);
    List<Chambre> findChambreByGroupe(Groupe groupe);
    Chambre findChambreById(String chambreId);
}

