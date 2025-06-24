package com.eyangless.Back.Repository;

import com.eyangless.Back.Entity.Cite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CiteRepository extends JpaRepository<Cite, String> {
    boolean existsByName(String name);
    public Cite findCiteById(String id);
}
