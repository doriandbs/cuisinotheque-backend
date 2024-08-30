package fr.cuisinotheque.backend.dtos;

import fr.cuisinotheque.data.entities.RecipeIngredientEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class IngredientDTO {
    private Long id;

    private String ingredient;
    private Double quantity;

}
