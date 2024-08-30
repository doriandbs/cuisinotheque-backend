
package fr.cuisinotheque.backend.services;

import fr.cuisinotheque.backend.dtos.UserDto;
import fr.cuisinotheque.backend.dtos.UserResponseDto;
import fr.cuisinotheque.data.entities.RoleEntity;

import java.util.List;



public interface IAccountService {
	
	
	RoleEntity addNewRole(RoleEntity role);
	
	void addRolesToUser(UserDto user, List<RoleEntity> roles);
	
	UserResponseDto addNewUser(UserDto userDto);
    UserResponseDto getUserById(Long id);
    List<UserResponseDto> getAllUsers();
    UserResponseDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);

	UserResponseDto getUserByUsername(String email);
}
