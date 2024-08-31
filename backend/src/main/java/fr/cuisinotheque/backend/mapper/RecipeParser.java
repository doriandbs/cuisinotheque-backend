package fr.cuisinotheque.backend.mapper;

import fr.cuisinotheque.backend.dtos.IngredientDTO;
import fr.cuisinotheque.backend.dtos.InstructionDTO;
import fr.cuisinotheque.backend.dtos.RecipeDTO;

import java.util.ArrayList;
import java.util.List;

public class RecipeParser {

    public static RecipeDTO parseRecipe(String recipeText) {
        RecipeDTO recipe = new RecipeDTO();
        List<IngredientDTO> ingredients = new ArrayList<>();
        List<InstructionDTO> instructions = new ArrayList<>();

        String[] sections = recipeText.split("\n\n");

        for (String section : sections) {
            if (section.startsWith("Titre :")) {
                recipe.setTitle(section.replace("Titre :", "").trim());
            } else if (section.startsWith("Ingrédients :")) {
                String[] ingredientLines = section.replace("Ingrédients :", "").trim().split("\n");
                for (String ingredientLine : ingredientLines) {
                    IngredientDTO ingredient = new IngredientDTO();
                    ingredient.setIngredient(ingredientLine.trim());
                    ingredients.add(ingredient);
                }
            } else if (section.startsWith("Instructions :")) {
                String[] instructionLines = section.replace("Instructions :", "").trim().split("\n");
                for (String instructionLine : instructionLines) {
                    InstructionDTO instruction = new InstructionDTO();
                    instruction.setInstruction(instructionLine.trim());
                    instructions.add(instruction);
                }
            }
        }

        recipe.setIngredients(ingredients);
        recipe.setInstructions(instructions);

        return recipe;
    }
}
