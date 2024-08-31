package fr.cuisinotheque.backend.services.impl;

import fr.cuisinotheque.backend.dtos.IngredientDTO;
import fr.cuisinotheque.backend.dtos.InstructionDTO;
import fr.cuisinotheque.backend.dtos.RecipeDTO;
import fr.cuisinotheque.backend.services.ICuisineAZRecipeScrapperService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CuisineAZRecipeScrapperServiceImpl implements ICuisineAZRecipeScrapperService {

    @Override
    public RecipeDTO cuisineAzScrapeRecipeFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        String title = doc.select("h1.recipe-title").first().text();

        Elements ingredientElements = doc.select("ul .ingredient_label");
        List<IngredientDTO> ingredients = new ArrayList<>();

        for (Element ingredientElement : ingredientElements) {
            String ingredientName = ingredientElement.text();

            String quantityText = ingredientElement.parent().select(".js-ingredient-qte").text();
            String[] quantityParts = quantityText.split(" ");
            Double quantity = quantityParts.length > 0 ? parseQuantity(quantityParts[0]) : 1.0;
            String unit = quantityParts.length > 1 ? quantityParts[1] : "";

            IngredientDTO ingredientDTO = new IngredientDTO();
            ingredientDTO.setIngredient(ingredientName + " " + unit);
            ingredientDTO.setQuantity(quantity);

            ingredients.add(ingredientDTO);
        }

        Elements instructionElements = doc.select("ul[data-onscroll] li p");
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

    private Double parseQuantity(String quantityText) {
        try {
            return Double.parseDouble(quantityText.replace(",", "."));
        } catch (NumberFormatException e) {
            return 1.0;
        }
    }
}
