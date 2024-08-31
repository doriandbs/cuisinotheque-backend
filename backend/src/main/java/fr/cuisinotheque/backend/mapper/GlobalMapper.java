package fr.cuisinotheque.backend.mapper;

import fr.cuisinotheque.backend.dtos.InstructionDTO;
import fr.cuisinotheque.backend.dtos.RecipeDTO;
import fr.cuisinotheque.backend.dtos.UserDto;
import fr.cuisinotheque.data.entities.InstructionEntity;
import fr.cuisinotheque.data.entities.RecipeEntity;
import fr.cuisinotheque.data.entities.RoleEntity;
import fr.cuisinotheque.data.entities.UserEntity;
import fr.cuisinotheque.data.enums.RoleName;
import fr.cuisinotheque.data.repositories.IIngredientRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class GlobalMapper {

    @Autowired
    private IIngredientRepository ingredientRepository;


    public abstract RecipeEntity mapRecipeToRecipeEntity(RecipeDTO recipe);

    public abstract List<InstructionEntity> instructionDTOListToInstructionEntityList(List<InstructionDTO> instructions);

    public abstract UserDto mapUserEntityToUser(UserEntity user);

    RoleName mapRoleEntiyToRoleName(RoleEntity value) {
        if (value == null) {
            return null;
        }
        return RoleName.valueOf(String.valueOf(value.getRoleName()));
    }


    public abstract List<RecipeDTO> toDtoList(List<RecipeEntity> entities);

    public abstract RecipeDTO mapRecipeEntityToRecipe(RecipeEntity recipeEntity);


}
