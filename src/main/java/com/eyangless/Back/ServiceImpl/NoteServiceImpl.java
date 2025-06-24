package com.eyangless.Back.ServiceImpl;

import com.eyangless.Back.DTO.NoteDTO;
import com.eyangless.Back.Entity.Cite;
import com.eyangless.Back.Entity.Note;
import com.eyangless.Back.Repository.CiteRepository;
import com.eyangless.Back.Repository.NoteRepository;
import com.eyangless.Back.Service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final CiteRepository citeRepository;
    private final NoteRepository noteRepository;

    @Override
    public Map<String ,Object> addNote(String citeid, NoteDTO dto) {
        Map<String, Object> response = new HashMap<>();
        Cite cite = citeRepository.findCiteById(citeid);
        if (cite == null) {
            response.put("message", "cette cite n'existe pas");
            return response;
        }
        Note note = new Note();
        note.setValue(dto.getValue());
        note.setComment(dto.getComment());
        Note note1 = noteRepository.save(note);
        cite.getNotes().add(note1);
        citeRepository.save(cite);
        response.put("message", "votre note a ete prise en compte");
        response.put("note", note1);

        return response;
    }
}
