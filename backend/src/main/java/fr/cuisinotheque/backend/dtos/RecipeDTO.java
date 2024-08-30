package fr.cuisinotheque.backend.dtos;


import lombok.Data;

import java.util.List;

@Data
public class RecipeDTO {
    private Long id;
    private String title;
    private List<IngredientDTO> ingredients;
    private List<InstructionDTO> instructions;
}
