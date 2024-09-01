package fr.cuisinotheque.backend.services;

import fr.cuisinotheque.backend.dtos.RecipeDTO;

import java.io.IOException;

public interface ICuisineActuelleRecipeScrapperService {
    RecipeDTO cuisineActuelleScrapeRecipeFromUrl(String url) throws IOException;

}
