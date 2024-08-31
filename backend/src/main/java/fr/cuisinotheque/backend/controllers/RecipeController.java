package fr.cuisinotheque.backend.controllers;

import fr.cuisinotheque.backend.dtos.RecipeDTO;
import fr.cuisinotheque.backend.services.ICuisineAZRecipeScrapperService;
import fr.cuisinotheque.backend.services.IMarmitonRecipeScrapperService;
import fr.cuisinotheque.backend.services.IRecipeService;
import fr.cuisinotheque.backend.services.IRicardoRecipeScrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/recipes")
@RequiredArgsConstructor
public class RecipeController {

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

}
