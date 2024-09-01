package fr.cuisinotheque.backend.services.impl;

import fr.cuisinotheque.backend.dtos.ImageRecipeDTO;
import fr.cuisinotheque.backend.dtos.IngredientDTO;
import fr.cuisinotheque.backend.dtos.InstructionDTO;
import fr.cuisinotheque.backend.dtos.RecipeDTO;
import fr.cuisinotheque.backend.services.IRicardoRecipeScrapperService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RicardoRecipeScrapperServiceImpl implements IRicardoRecipeScrapperService {
    @Override
    public RecipeDTO ricardoScrapeRecipeFromUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();

        String title = doc.select("h1.c-recipe__heading-title").first().text();

        Elements ingredientElements = doc.select(".c-recipe-instructions--ingredients ul .c-recipe-instructions__item div");
        List<IngredientDTO> ingredients = new ArrayList<>();
        Set<String> seenIngredients = new HashSet<>();

        for (Element ingredientElement : ingredientElements) {
            String ingredientName = ingredientElement.text();
            if (seenIngredients.add(ingredientName) && !ingredientName.isBlank()) {
                IngredientDTO ingredientDTO = new IngredientDTO();
                ingredientDTO.setIngredient(ingredientName);
                ingredients.add(ingredientDTO);
            }
        }

        Elements instructionElements = doc.select(".c-recipe-instructions--preparation ul .c-recipe-instructions__item div");
        List<InstructionDTO> instructions = new ArrayList<>();
        Set<String> seenInstructions = new HashSet<>();

        for (Element instructionElement : instructionElements) {
            String stepText = instructionElement.text();
            if (seenInstructions.add(stepText) && !stepText.isBlank()) {
                InstructionDTO instructionDTO = new InstructionDTO();
                instructionDTO.setInstruction(stepText);
                instructions.add(instructionDTO);
            }
        }

        Elements imageElements = doc.select("div.c-recipe-picture img");
        List<ImageRecipeDTO> images = new ArrayList<>();

        for (Element img : imageElements) {
            String imgUrl = img.attr("srcset");
            if (!imgUrl.isEmpty()) {
                String[] urls = imgUrl.split(", ");
                if (urls.length > 0) {
                    imgUrl = urls[urls.length - 1].split(" ")[0];
                }

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
