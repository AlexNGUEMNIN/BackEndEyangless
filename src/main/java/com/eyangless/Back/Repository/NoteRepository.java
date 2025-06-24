package com.eyangless.Back.Repository;

import com.eyangless.Back.Entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, String> {
    Note findNoteById(String id);
}
