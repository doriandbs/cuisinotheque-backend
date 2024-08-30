package fr.cuisinotheque.backend.services;

import fr.cuisinotheque.backend.dtos.RecipeDTO;

import java.io.IOException;

public interface IMarmitonRecipeScrapperService {
    RecipeDTO marmitonScrapeRecipeFromUrl(String url) throws IOException;
}
