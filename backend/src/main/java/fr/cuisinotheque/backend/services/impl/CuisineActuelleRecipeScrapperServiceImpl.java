package fr.cuisinotheque.backend.services.impl;

import fr.cuisinotheque.backend.dtos.IngredientDTO;
import fr.cuisinotheque.backend.dtos.InstructionDTO;
import fr.cuisinotheque.backend.dtos.RecipeDTO;
import fr.cuisinotheque.backend.services.ICuisineActuelleRecipeScrapperService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CuisineActuelleRecipeScrapperServiceImpl implements ICuisineActuelleRecipeScrapperService {

    public RecipeDTO cuisineActuelleScrapeRecipeFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        String title = doc.select("h1.articleTitle.recipe-title").text();

        Elements ingredientElements = doc.select("ul.recipeIngredients-list li.recipeIngredients-ingredient");
        List<IngredientDTO> ingredients = new ArrayList<>();

        for (Element ingredientElement : ingredientElements) {
            String quantityText = ingredientElement.select("span.recipeIngredients-quantity").text();
            Double quantity = quantityText.isEmpty() ? 1.0 : Double.parseDouble(quantityText.replace(",", "."));

            String unit = ingredientElement.select("span[data-block=recipe-ingredientUnit]").text();
            String ingredientName = ingredientElement.select("span[data-block=recipe-ingredientLabel]").text();

            IngredientDTO ingredientDTO = new IngredientDTO();
            ingredientDTO.setQuantity(quantity);

            if (!unit.isEmpty()) {
                ingredientDTO.setIngredient(quantityText + " " + unit + " de " + ingredientName);
            } else {
                ingredientDTO.setIngredient(quantityText + " " + ingredientName);
            }

            ingredients.add(ingredientDTO);
        }

        // Extraire les instructions
        Elements instructionElements = doc.select("div.recipe-instructionContent ol li");
        List<InstructionDTO> instructions = new ArrayList<>();

        for (Element instructionElement : instructionElements) {
            String stepText = instructionElement.text();

            InstructionDTO instructionDTO = new InstructionDTO();
            instructionDTO.setInstruction(stepText);
            instructions.add(instructionDTO);
        }

        // Construire l'objet RecipeDTO
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setTitle(title);
        recipeDTO.setIngredients(ingredients);
        recipeDTO.setInstructions(instructions);

        return recipeDTO;
    }

}
