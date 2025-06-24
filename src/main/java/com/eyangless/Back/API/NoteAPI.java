package com.eyangless.Back.API;

import com.eyangless.Back.DTO.NoteDTO;
import com.eyangless.Back.ServiceImpl.NoteServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteAPI {
    private final NoteServiceImpl noteService;

    @PostMapping("/{citeid}")
    public ResponseEntity<Map<String, Object>> addnote(@PathVariable String citeid, @RequestBody NoteDTO noteDTO) {
        return ResponseEntity.ok(noteService.addNote(citeid, noteDTO));
    }
}
