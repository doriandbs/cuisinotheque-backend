package fr.cuisinotheque.backend.services;

import fr.cuisinotheque.backend.dtos.RecipeDTO;

import java.io.IOException;

public interface ICuisineAZRecipeScrapperService {
    RecipeDTO cuisineAzScrapeRecipeFromUrl(String url) throws IOException;

}
