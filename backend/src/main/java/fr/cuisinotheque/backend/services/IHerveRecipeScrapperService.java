package fr.cuisinotheque.backend.services;

import fr.cuisinotheque.backend.dtos.RecipeDTO;

import java.io.IOException;

public interface IHerveRecipeScrapperService {
    RecipeDTO herveScrapeRecipeFromUrl(String url) throws IOException;

}
