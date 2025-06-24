package com.eyangless.Back.Entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class File {
    private String filesrc;
}
