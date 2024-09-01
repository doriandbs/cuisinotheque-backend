package fr.cuisinotheque.backend.services.impl;

import fr.cuisinotheque.backend.dtos.ImageRecipeDTO;
import fr.cuisinotheque.backend.dtos.IngredientDTO;
import fr.cuisinotheque.backend.dtos.InstructionDTO;
import fr.cuisinotheque.backend.dtos.RecipeDTO;
import fr.cuisinotheque.backend.services.IMarmitonRecipeScrapperService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class MarmitonRecipeScrapperServiceImpl implements IMarmitonRecipeScrapperService {
    @Override
    public RecipeDTO marmitonScrapeRecipeFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        String title = doc.select("h1").first().text();

        Elements ingredientElements = doc.select(".card-ingredient");
        List<IngredientDTO> ingredients = new ArrayList<>();

        for (Element ingredientElement : ingredientElements) {
            String ingredientName = ingredientElement.select(".ingredient-name").text();

            String quantityText = ingredientElement.select(".card-ingredient-quantity .count").text();
            Double quantity = quantityText.isEmpty() ? 1.0 : Double.parseDouble(quantityText.replace(",", "."));

            String unit = ingredientElement.select(".card-ingredient-quantity .unit").text();

            String complement = ingredientElement.select(".ingredient-complement").text();

            IngredientDTO ingredientDTO = new IngredientDTO();
            ingredientDTO.setIngredient(ingredientName);
            ingredientDTO.setQuantity(quantity);
            if (!unit.isEmpty()) {
                ingredientDTO.setIngredient(quantityText + " " + unit + " de " + ingredientDTO.getIngredient());
            } else {
                ingredientDTO.setIngredient(quantityText + " " + ingredientDTO.getIngredient());
            }

            if (!complement.isEmpty()) {
                ingredientDTO.setIngredient(ingredientDTO.getIngredient() + " " + complement);
            }
            ingredients.add(ingredientDTO);
        }

        Elements instructionElements = doc.select(".recipe-step-list__container");
        List<InstructionDTO> instructions = new ArrayList<>();

        for (Element instructionElement : instructionElements) {
            String stepText = instructionElement.select("p").text();

            InstructionDTO instructionDTO = new InstructionDTO();
            instructionDTO.setInstruction(stepText);
            instructions.add(instructionDTO);
        }

        Elements imageElements = doc.select(".recipe-media-viewer-thumbnail-container img");
        List<ImageRecipeDTO> images = new ArrayList<>();
        for (Element img : imageElements) {
            String imgUrl = img.attr("data-src");
            if (!imgUrl.isEmpty()) {
                byte[] imageData = downloadImage(imgUrl);
                ImageRecipeDTO imageRecipeDTO = new ImageRecipeDTO();
                imageRecipeDTO.setImageData(imageData);
                images.add(imageRecipeDTO);
            }
        }

        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setTitle(title);
        recipeDTO.setIngredients(ingredients);
        recipeDTO.setInstructions(instructions);
        recipeDTO.setImages(images);

        return recipeDTO;
    }

    private byte[] downloadImage(String imageUrl) throws IOException {
        try (InputStream in = new URL(imageUrl).openStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        }
    }
}


