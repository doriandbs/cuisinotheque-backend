package fr.cuisinotheque.data.repositories;

import fr.cuisinotheque.data.entities.RecipeImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRecipeImageRepository extends JpaRepository<RecipeImageEntity, Long> {

}
