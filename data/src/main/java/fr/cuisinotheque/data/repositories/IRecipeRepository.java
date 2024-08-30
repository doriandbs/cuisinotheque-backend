package fr.cuisinotheque.data.repositories;

import fr.cuisinotheque.data.entities.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRecipeRepository extends JpaRepository<RecipeEntity, Long> {
}