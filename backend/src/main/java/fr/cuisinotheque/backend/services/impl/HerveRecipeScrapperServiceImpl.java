package fr.cuisinotheque.backend.services.impl;

import fr.cuisinotheque.backend.dtos.IngredientDTO;
import fr.cuisinotheque.backend.dtos.InstructionDTO;
import fr.cuisinotheque.backend.dtos.RecipeDTO;
import fr.cuisinotheque.backend.services.IHerveRecipeScrapperService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class HerveRecipeScrapperServiceImpl implements IHerveRecipeScrapperService {

    @Override
    public RecipeDTO herveScrapeRecipeFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        String title = doc.select("header.post-header h1.post-title").first().text();

        Elements ingredientElements = doc.select("div.recipe-ingredient-list ul li");
        List<IngredientDTO> ingredients = new ArrayList<>();

        for (Element ingredientElement : ingredientElements) {
            String ingredientText = ingredientElement.text();

            IngredientDTO ingredientDTO = new IngredientDTO();
            ingredientDTO.setIngredient(ingredientText);

            ingredients.add(ingredientDTO);
        }

        Elements instructionElements = doc.select("div.recipe-steps ol li");
        List<InstructionDTO> instructions = new ArrayList<>();

        for (Element instructionElement : instructionElements) {
            String stepText = instructionElement.text();

            InstructionDTO instructionDTO = new InstructionDTO();
            instructionDTO.setInstruction(stepText);
            instructions.add(instructionDTO);
        }

        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setTitle(title);
        recipeDTO.setIngredients(ingredients);
        recipeDTO.setInstructions(instructions);

        return recipeDTO;
    }
}
