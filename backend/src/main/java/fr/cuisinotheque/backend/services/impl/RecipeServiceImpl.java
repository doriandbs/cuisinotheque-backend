package fr.cuisinotheque.backend.services.impl;

import fr.cuisinotheque.backend.dtos.RecipeDTO;
import fr.cuisinotheque.backend.mapper.GlobalMapper;
import fr.cuisinotheque.backend.services.IRecipeService;
import fr.cuisinotheque.data.entities.InstructionEntity;
import fr.cuisinotheque.data.entities.RecipeEntity;
import fr.cuisinotheque.data.entities.RecipeIngredientEntity;
import fr.cuisinotheque.data.repositories.IIngredientRepository;
import fr.cuisinotheque.data.repositories.IInstructionRepository;
import fr.cuisinotheque.data.repositories.IRecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements IRecipeService {
    private final IRecipeRepository recipeRepository;
    private final IInstructionRepository instructionRepository;

    private final IIngredientRepository ingredientRepository;

    private final GlobalMapper recipeMapper;

    public List<RecipeDTO> getAllRecipes() {
        return recipeMapper.toDtoList(recipeRepository.findAll());
    }

    public RecipeDTO getRecipeById(Long id) {
        Optional<RecipeEntity> recipeEntity = recipeRepository.findById(id);
        return recipeEntity.map(recipeMapper::mapRecipeEntityToRecipe).orElse(null);
    }

    public RecipeDTO createRecipe(RecipeDTO recipeDTO) {
        RecipeEntity recipeEntity = recipeMapper.mapRecipeToRecipeEntity(recipeDTO);
        RecipeEntity savedEntity = recipeRepository.save(recipeEntity);
        return recipeMapper.mapRecipeEntityToRecipe(savedEntity);
    }

    public RecipeDTO updateRecipe(Long id, RecipeDTO updatedRecipeDTO) {
        return recipeRepository.findById(id).map(recipeEntity -> {
            recipeEntity.setTitle(updatedRecipeDTO.getTitle());

            List<RecipeIngredientEntity> updatedRecipeIngredients = updatedRecipeDTO.getIngredients().stream()
                    .map(ingredientDTO -> {
                        RecipeIngredientEntity recipeIngredientEntity = new RecipeIngredientEntity();
                        recipeIngredientEntity.setRecipe(recipeEntity);
                        recipeIngredientEntity.setIngredient(ingredientRepository.findById(ingredientDTO.getId())
                                .orElseThrow(() -> new RuntimeException("Ingredient not found")));
                        recipeIngredientEntity.setQuantity(ingredientDTO.getQuantity());
                        return recipeIngredientEntity;
                    }).collect(Collectors.toList());

            recipeEntity.getRecipeIngredients().clear();
            recipeEntity.getRecipeIngredients().addAll(updatedRecipeIngredients);

            List<InstructionEntity> updatedInstructions = updatedRecipeDTO.getInstructions().stream()
                    .map(instructionDTO -> instructionRepository.findById(instructionDTO.getId())
                            .orElseThrow(() -> new RuntimeException("Instruction not found")))
                    .collect(Collectors.toList());

            recipeEntity.getInstructions().clear();
            recipeEntity.getInstructions().addAll(updatedInstructions);

            RecipeEntity updatedEntity = recipeRepository.save(recipeEntity);
            return recipeMapper.mapRecipeEntityToRecipe(updatedEntity);
        }).orElseGet(() -> {
            RecipeEntity newEntity = recipeMapper.mapRecipeToRecipeEntity(updatedRecipeDTO);
            newEntity.setId(id);

            List<RecipeIngredientEntity> newRecipeIngredients = updatedRecipeDTO.getIngredients().stream()
                    .map(ingredientDTO -> {
                        RecipeIngredientEntity recipeIngredientEntity = new RecipeIngredientEntity();
                        recipeIngredientEntity.setRecipe(newEntity);
                        recipeIngredientEntity.setIngredient(ingredientRepository.findById(ingredientDTO.getId())
                                .orElseThrow(() -> new RuntimeException("Ingredient not found")));
                        recipeIngredientEntity.setQuantity(ingredientDTO.getQuantity());
                        return recipeIngredientEntity;
                    }).collect(Collectors.toList());

            newEntity.setRecipeIngredients(newRecipeIngredients);

            List<InstructionEntity> newInstructions = updatedRecipeDTO.getInstructions().stream()
                    .map(instructionDTO -> instructionRepository.findById(instructionDTO.getId())
                            .orElseThrow(() -> new RuntimeException("Instruction not found")))
                    .collect(Collectors.toList());

            newEntity.setInstructions(newInstructions);

            RecipeEntity savedEntity = recipeRepository.save(newEntity);
            return recipeMapper.mapRecipeEntityToRecipe(savedEntity);
        });
    }


    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }
}
