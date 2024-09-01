package fr.cuisinotheque.backend.services;

import fr.cuisinotheque.backend.dtos.RecipeDTO;

import java.io.IOException;

public interface IPatissiersRecipeScrapperService {
    RecipeDTO patissiersScrapeRecipeFromUrl(String url) throws IOException;

}
