package fr.cuisinotheque.backend.controllers;

import fr.cuisinotheque.backend.dtos.RecipeDTO;
import fr.cuisinotheque.backend.services.IMarmitonRecipeScrapperService;
import fr.cuisinotheque.backend.services.IRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/recipes")
public class RecipeController {

    @Autowired
    private IRecipeService recipeService;

    @Autowired
    private IMarmitonRecipeScrapperService recipeScrapperService;

    @GetMapping
    public List<RecipeDTO> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @GetMapping("/{id}")
    public RecipeDTO getRecipeById(@PathVariable Long id) {
        return recipeService.getRecipeById(id);
    }

    @PostMapping
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
        if (url.contains("marmiton.org")) {
            return recipeScrapperService.marmitonScrapeRecipeFromUrl(url);
        } else if (url.contains("autresite.com")) {
            return null;
        } else {
            throw new IllegalArgumentException("URL non prise en charge pour le scraping");
        }
    }

}
