package fr.cuisinotheque.backend.mapper;

import fr.cuisinotheque.backend.dtos.RecipeDTO;
import fr.cuisinotheque.data.entities.RecipeEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GlobalMapper {
    RecipeDTO mapRecipeEntityToRecipe(RecipeEntity recipeEntity);

    RecipeEntity mapRecipeToRecipeEntity(RecipeDTO recipe);

    List<RecipeDTO> toDtoList(List<RecipeEntity> entities);

    List<RecipeEntity> toEntityList(List<RecipeDTO> dtos);
}
