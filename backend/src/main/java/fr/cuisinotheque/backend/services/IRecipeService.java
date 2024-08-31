package fr.cuisinotheque.backend.services;

import fr.cuisinotheque.backend.dtos.RecipeDTO;

import java.util.List;

public interface IRecipeService {
    List<RecipeDTO> getAllRecipes();
    RecipeDTO getRecipeById(Long id);
    RecipeDTO createRecipe(RecipeDTO recipeDTO);
    RecipeDTO updateRecipe(Long id, RecipeDTO updatedRecipeDTO);
    void deleteRecipe(Long id);

    List<RecipeDTO> getRecipesByCurrentUser();
}
