
package fr.cuisinotheque.data.entities;

import fr.cuisinotheque.data.enums.RoleName;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="roles")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity {
	  @Id
	  @GeneratedValue(strategy= GenerationType.AUTO)
	  private Long id;

	  @Enumerated(EnumType.STRING)
	  private RoleName roleName;

	  public RoleEntity(RoleName roleName) {
		  this.roleName = roleName;
	  }
}

