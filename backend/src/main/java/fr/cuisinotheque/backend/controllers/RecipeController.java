package fr.cuisinotheque.backend.controllers;

import fr.cuisinotheque.backend.dtos.RecipeDTO;
import fr.cuisinotheque.backend.mapper.RecipeParser;
import fr.cuisinotheque.backend.services.ICuisineAZRecipeScrapperService;
import fr.cuisinotheque.backend.services.IMarmitonRecipeScrapperService;
import fr.cuisinotheque.backend.services.IRecipeService;
import fr.cuisinotheque.backend.services.IRicardoRecipeScrapperService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@RestController
@RequestMapping("api/v1/recipes")
@RequiredArgsConstructor
public class RecipeController {

    @Value("${openai.api.key}")
    private String apiKey;

    private final IRecipeService recipeService;

    private final IMarmitonRecipeScrapperService marmitonRecipeScrapperService;

    private final ICuisineAZRecipeScrapperService cuisineAZRecipeScrapperService;

    @Autowired
    private IRicardoRecipeScrapperService ricardoRecipeScrapperService;

    @GetMapping("/all")
    public List<RecipeDTO> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @GetMapping("/mines")
    public List<RecipeDTO> getMyRecipes() {
        return recipeService.getRecipesByCurrentUser();
    }

    @GetMapping("/{id}")
    public RecipeDTO getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id);
    }

    @PostMapping("/create")
    public RecipeDTO createRecipe(@RequestBody RecipeDTO recipe) {
        return recipeService.createRecipe(recipe);
    }

    @PutMapping("/{id}")
    public RecipeDTO updateRecipe(@PathVariable Long id, @RequestBody RecipeDTO updatedRecipe) {
        return recipeService.updateRecipe(id, updatedRecipe);
    }

    @DeleteMapping("/{id}")
    public void deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
    }

    @GetMapping("/scrape")
    public RecipeDTO scrapeRecipeFromUrl(@RequestParam String url) throws IOException {
        if (url.contains("marmiton")) {
            return marmitonRecipeScrapperService.marmitonScrapeRecipeFromUrl(url);
        } else if (url.contains("cuisineaz")) {
            return cuisineAZRecipeScrapperService.cuisineAzScrapeRecipeFromUrl(url);
        } else if (url.contains("ricardocuisine")) {
            return ricardoRecipeScrapperService.ricardoScrapeRecipeFromUrl(url);
        } else {
            throw new IllegalArgumentException("URL non prise en charge pour le scraping");
        }
    }

    @GetMapping("/openai")
    public RecipeDTO getFromOpenai(String message) {
        String url = "https://api.openai.com/v1/chat/completions";
        String model = "gpt-4o-2024-08-06";
        message = message.replace("\n", " ");
        message = "Attention, je veux que tu me fasses un résumé de cette recette. Je veux de la forme Titre :, saut de ligne, Ingrédients : et tu listes les ingrédients, saut de ligne Instructions : Liste des instructions pour réaliser la recette. Ca doit être uniforme et propre. " + message;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");

            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\":\"" + message + "\"}]}";
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            String content = jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
            RecipeDTO recipe = RecipeParser.parseRecipe(content);

            return recipe;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
