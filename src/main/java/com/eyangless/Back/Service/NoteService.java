package com.eyangless.Back.Service;

import com.eyangless.Back.DTO.NoteDTO;

import java.util.Map;

public interface NoteService {
    Map<String ,Object> addNote(String citeid, NoteDTO dto);
}
