package fr.cuisinotheque.data.repositories;

import fr.cuisinotheque.data.entities.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IIngredientRepository extends JpaRepository<IngredientEntity, Long> {
}
