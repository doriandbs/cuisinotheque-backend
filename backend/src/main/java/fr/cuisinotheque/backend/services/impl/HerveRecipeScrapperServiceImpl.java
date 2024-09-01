package fr.cuisinotheque.backend.services.impl;

import fr.cuisinotheque.backend.dtos.ImageRecipeDTO;
import fr.cuisinotheque.backend.dtos.IngredientDTO;
import fr.cuisinotheque.backend.dtos.InstructionDTO;
import fr.cuisinotheque.backend.dtos.RecipeDTO;
import fr.cuisinotheque.backend.services.IHerveRecipeScrapperService;
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

        Elements imageElements = doc.select(".recipe-image img.lazyload");
        List<ImageRecipeDTO> images = new ArrayList<>();

        for (Element img : imageElements) {
            String imgUrl = img.attr("data-src-webp");
            if (imgUrl.isEmpty()) {
                imgUrl = img.attr("data-src"); // Utiliser l'attribut data-src si data-src-webp est vide
            }
            if (!imgUrl.isEmpty() && !imgUrl.contains("pinterest")) {
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
