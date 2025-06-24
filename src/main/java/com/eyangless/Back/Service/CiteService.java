package com.eyangless.Back.Service;




import com.eyangless.Back.DTO.ChambreDTO;
import com.eyangless.Back.DTO.CiteDTO;
import com.eyangless.Back.DTO.NoteDTO;
import com.eyangless.Back.Entity.Cite;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface CiteService {
    CiteDTO createCite(ChambreDTO dto);

    CiteDTO createCite(CiteDTO dto, String authHeader);

    CiteDTO updateCite(CiteDTO dto, String authHeader);
    Cite getCiteById(String id);
    Page<Cite> getAllCites(Integer pageNo, Integer pageSize);
    String deleteCite(String id, String authHeader);
}
