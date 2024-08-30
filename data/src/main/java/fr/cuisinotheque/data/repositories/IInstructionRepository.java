package fr.cuisinotheque.data.repositories;

import fr.cuisinotheque.data.entities.InstructionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IInstructionRepository extends JpaRepository<InstructionEntity, Long> {
}
