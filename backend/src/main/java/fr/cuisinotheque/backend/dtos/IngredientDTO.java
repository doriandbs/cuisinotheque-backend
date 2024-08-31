package fr.cuisinotheque.backend.dtos;

import lombok.Data;

@Data
public class IngredientDTO {
    private Long id;

    private String ingredient;
    private Double quantity;

}
