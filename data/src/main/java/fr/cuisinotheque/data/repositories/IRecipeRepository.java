package fr.cuisinotheque.data.repositories;

import fr.cuisinotheque.data.entities.RecipeEntity;
import fr.cuisinotheque.data.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRecipeRepository extends JpaRepository<RecipeEntity, Long> {
    List<RecipeEntity> findByUser(UserEntity user);

}