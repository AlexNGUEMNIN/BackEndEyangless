package com.eyangless.Back.Repository;


import com.eyangless.Back.Entity.Groupe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface GroupeRepository extends JpaRepository<Groupe, String> {
    List<Groupe> findByCiteId(String citeId, Pageable paging);
    Groupe findGroupeById(String groupeId);
}
