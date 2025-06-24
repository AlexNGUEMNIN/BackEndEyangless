package com.eyangless.Back.Repository;

import com.eyangless.Back.Entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, String> {
    Contact findContactById(String id);
}
