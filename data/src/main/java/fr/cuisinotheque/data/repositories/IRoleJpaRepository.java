
package fr.cuisinotheque.data.repositories;


import fr.cuisinotheque.data.entities.RoleEntity;
import fr.cuisinotheque.data.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRoleJpaRepository extends JpaRepository<RoleEntity, Long> {
	RoleEntity findByRoleName(RoleName roleName);
}

