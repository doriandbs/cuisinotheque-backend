
package fr.cuisinotheque.backend;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("fr.cuisinotheque.data.repositories")
@EntityScan("fr.cuisinotheque.data.entities")
public class Main {
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

/*	@Bean
	CommandLineRunner start(IAccountService accountService, IUserJpaRepository userRepository,
							IRoleJpaRepository roleRepository) {
		return args -> {
			RoleEntity admin = new RoleEntity(RoleName.ADMIN);
			RoleEntity user = new RoleEntity(RoleName.USER);

			accountService.addNewRole(user);
			accountService.addNewRole(admin);

			UserDto userTemp = new UserDto(null, "Dorian", "Dubois", "dodo@gmail.com", "0000", null);
			accountService.addNewUser(userTemp);

			ArrayList<RoleEntity> roles = new ArrayList<>();
			roles.add(user);
			roles.add(admin);
			accountService.addRolesToUser(userTemp, roles);
		};
	}*/

}
