package com.eyangless.Back.DTO;

import com.eyangless.Back.Entity.Caracteristique;
import com.eyangless.Back.Entity.File;
import lombok.Data;
import java.util.List;

@Data
public class GroupeDTO {
    private String id;
    private int superficie;
    private String citeId;
    private List<Caracteristique> caracteristiques;
    private List<File> picturePaths; // ou URLs des images
}
