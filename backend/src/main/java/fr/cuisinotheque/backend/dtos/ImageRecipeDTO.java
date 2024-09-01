package fr.cuisinotheque.backend.dtos;

import lombok.Data;

@Data
public class ImageRecipeDTO {
    private Long id;
    private byte[] imageData;
}
