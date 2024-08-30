package fr.cuisinotheque.backend.services.impl;

import java.util.List;
import java.util.stream.Collectors;


import fr.cuisinotheque.backend.dtos.UserDto;
import fr.cuisinotheque.backend.dtos.UserResponseDto;
import fr.cuisinotheque.backend.services.IAccountService;
import fr.cuisinotheque.data.entities.RoleEntity;
import fr.cuisinotheque.data.entities.UserEntity;
import fr.cuisinotheque.data.repositories.IRoleJpaRepository;
import fr.cuisinotheque.data.repositories.IUserJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements IAccountService, UserDetailsService {

    private final IUserJpaRepository userJpaRepository;
    private final IRoleJpaRepository roleJpaRepository;
    private final PasswordEncoder passwordEncoder;
    
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		return userJpaRepository.findByEmail(email)
				.orElseThrow(()-> new UsernameNotFoundException("Cet utilisateur n'existe pas"));
	}
	
	@Override
    public UserResponseDto getUserByUsername(String email) {
        UserEntity user = userJpaRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Cet utilisateur n'existe pas"));
        return mapToUserResponseDto(user);
    }

	@Override
	public RoleEntity addNewRole(RoleEntity role) {
		return roleJpaRepository.save(role);
	}

	@Override
	public void addRolesToUser(UserDto user, List<RoleEntity> roles) {
		UserEntity userDb = userJpaRepository.findByEmail(user.getEmail()).orElse(null);
		roles.stream()
			.map(RoleEntity::getRoleName)
			.map(roleJpaRepository::findByRoleName)
			.forEach(userDb.getRoles()::add);
	}

	

	@Override
    public UserResponseDto addNewUser(UserDto userDto) {
        UserEntity user = new UserEntity();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        
        
        UserEntity savedUser = userJpaRepository.save(user);
        return mapToUserResponseDto(savedUser);
    }
	
	
	@Override
    public UserResponseDto getUserById(Long id) {
        UserEntity user = userJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return mapToUserResponseDto(user);
    }

	@Override
    public List<UserResponseDto> getAllUsers() {
        List<UserEntity> users = userJpaRepository.findAll();
        return users.stream()
                .map(this::mapToUserResponseDto)
                .collect(Collectors.toList());
    }

	@Override
    public UserResponseDto updateUser(Long id, UserDto userDto) {
        UserEntity user = userJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        System.out.println("USER" + user.getFirstName());
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        
        UserEntity updatedUser = userJpaRepository.save(user);
        return mapToUserResponseDto(updatedUser);
    }


    @Override
    public void deleteUser(Long id) {
        userJpaRepository.deleteById(id);
    }
	
	
    private UserResponseDto mapToUserResponseDto(UserEntity user) {
        UserResponseDto.UserResponseDtoBuilder builder = UserResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail());

        if (user.getRoles()!=null && !user.getRoles().isEmpty()) {
            builder.roles(user.getRoles().stream()
                    .map(RoleEntity::getRoleName)
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }

}