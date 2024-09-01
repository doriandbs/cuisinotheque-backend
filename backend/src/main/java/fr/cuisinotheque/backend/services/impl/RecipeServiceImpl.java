package fr.cuisinotheque.backend.services.impl;

import fr.cuisinotheque.backend.dtos.ImageRecipeDTO;
import fr.cuisinotheque.backend.dtos.IngredientDTO;
import fr.cuisinotheque.backend.dtos.InstructionDTO;
import fr.cuisinotheque.backend.dtos.RecipeDTO;
import fr.cuisinotheque.backend.mapper.GlobalMapper;
import fr.cuisinotheque.backend.services.IRecipeService;
import fr.cuisinotheque.data.entities.*;
import fr.cuisinotheque.data.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements IRecipeService {
    private final IRecipeRepository recipeRepository;
    private final IInstructionRepository instructionRepository;
    private final IUserJpaRepository userJpaRepository;
    private final IIngredientRepository ingredientRepository;
    private final IRecipeImageRepository recipeImageRepository;
    private final GlobalMapper recipeMapper;


    public List<RecipeDTO> getAllRecipes() {
        return recipeMapper.toDtoList(recipeRepository.findAll());
    }

    public List<RecipeDTO> getRecipesByCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserEntity userEntity = userJpaRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserEntity user = userJpaRepository.findByEmail(userEntity.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return recipeMapper.toDtoList(recipeRepository.findByUser(user));
        }
        return null;
    }

    public RecipeDTO getRecipeById(Long id) {
        Optional<RecipeEntity> recipeEntity = recipeRepository.findById(id);
        return recipeEntity.map(recipeMapper::mapRecipeEntityToRecipe).orElse(null);
    }


    public RecipeDTO createRecipe(RecipeDTO recipeDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        RecipeEntity recipeEntity = recipeMapper.mapRecipeToRecipeEntity(recipeDTO);

        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            UserEntity userEntity = userJpaRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            recipeEntity.setUser(userEntity);
        }
        List<InstructionEntity> instructions = new ArrayList<>();
        for (InstructionDTO instructionDTO : recipeDTO.getInstructions()) {
            InstructionEntity instructionEntity = new InstructionEntity();
            if (instructionEntity.getId() != null) {
                instructionEntity = instructionRepository.findById(instructionDTO.getId())
                        .orElse(null);
            } else {
                instructionEntity.setInstruction(instructionDTO.getInstruction());
                instructionEntity = instructionRepository.save(instructionEntity);
            }

            instructions.add(instructionEntity);
        }

        List<IngredientEntity> ingredients = new ArrayList<>();
        for (IngredientDTO ingredientDTO : recipeDTO.getIngredients()) {
            IngredientEntity ingredientEntity = new IngredientEntity();
            if (ingredientEntity.getId() != null) {
                ingredientEntity = ingredientRepository.findById(ingredientDTO.getId())
                        .orElse(null);
            } else {
                ingredientEntity.setIngredient(ingredientDTO.getIngredient());
                ingredientEntity = ingredientRepository.save(ingredientEntity);
            }

            ingredients.add(ingredientEntity);
        }
        recipeEntity.setInstructions(instructions);
        recipeEntity.setIngredients(ingredients);
        RecipeEntity savedRecipeEntity = recipeRepository.save(recipeEntity);
        List<RecipeImageEntity> imageEntities = new ArrayList<>();
        if (recipeDTO.getImages() != null) {
            for (ImageRecipeDTO imageDTO : recipeDTO.getImages()) {
                RecipeImageEntity imageEntity = new RecipeImageEntity();
                imageEntity.setImageData(imageDTO.getImageData());
                imageEntity.setRecipe(savedRecipeEntity);
                RecipeImageEntity savedImageEntity = recipeImageRepository.save(imageEntity);
                imageEntities.add(savedImageEntity);
            }

        }
        savedRecipeEntity.setImages(imageEntities);
        RecipeEntity savedRecipeEntityImage = recipeRepository.save(recipeEntity);

        return recipeMapper.mapRecipeEntityToRecipe(savedRecipeEntityImage);
    }

    public RecipeDTO updateRecipe(Long id, RecipeDTO updatedRecipeDTO) {
        return recipeRepository.findById(id).map(recipeEntity -> {
            recipeEntity.setTitle(updatedRecipeDTO.getTitle());

            List<IngredientEntity> updatedIngredients = updatedRecipeDTO.getIngredients().stream()
                    .map(ingredientDTO -> ingredientRepository.findById(ingredientDTO.getId())
                            .orElseThrow(() -> new RuntimeException("Ingredient not found")))
                    .toList();

            recipeEntity.getIngredients().clear();
            recipeEntity.getIngredients().addAll(updatedIngredients);

            List<InstructionEntity> updatedInstructions = updatedRecipeDTO.getInstructions().stream()
                    .map(instructionDTO -> instructionRepository.findById(instructionDTO.getId())
                            .orElseThrow(() -> new RuntimeException("Instruction not found")))
                    .toList();

            recipeEntity.getInstructions().clear();
            recipeEntity.getInstructions().addAll(updatedInstructions);

            RecipeEntity updatedEntity = recipeRepository.save(recipeEntity);
            return recipeMapper.mapRecipeEntityToRecipe(updatedEntity);
        }).orElseGet(() -> {
            RecipeEntity newEntity = recipeMapper.mapRecipeToRecipeEntity(updatedRecipeDTO);
            newEntity.setId(id);

            List<IngredientEntity> newIngredients = updatedRecipeDTO.getIngredients().stream()
                    .map(ingredientDTO -> ingredientRepository.findById(ingredientDTO.getId())
                            .orElseThrow(() -> new RuntimeException("Instruction not found")))
                    .toList();

            newEntity.setIngredients(newIngredients);


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
