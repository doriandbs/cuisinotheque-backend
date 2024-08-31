package fr.cuisinotheque.backend.services;

import fr.cuisinotheque.backend.dtos.RecipeDTO;

import java.io.IOException;

public interface IRicardoRecipeScrapperService {
    RecipeDTO ricardoScrapeRecipeFromUrl(String url) throws IOException;

}
