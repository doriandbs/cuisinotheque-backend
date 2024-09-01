
package fr.cuisinotheque.backend.services;

import fr.cuisinotheque.backend.dtos.AccountResponseDto;
import fr.cuisinotheque.backend.dtos.LoginRequestDto;
import fr.cuisinotheque.backend.dtos.RegisterRequestDto;
import fr.cuisinotheque.backend.mapper.GlobalMapper;
import fr.cuisinotheque.backend.security.CustomPasswordEncoder;
import fr.cuisinotheque.data.entities.RoleEntity;
import fr.cuisinotheque.data.entities.UserEntity;
import fr.cuisinotheque.data.enums.RoleName;
import fr.cuisinotheque.data.repositories.IRoleJpaRepository;
import fr.cuisinotheque.data.repositories.IUserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final IUserJpaRepository userJpaRepository;

    private final CustomPasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final IRoleJpaRepository roleJpaRepository;

    private final JwtService jwtService;
    private final GlobalMapper globalMapper;


    public ResponseEntity<AccountResponseDto> register(RegisterRequestDto user) {
        if(userJpaRepository.findByEmail(user.getEmail()).isPresent()){
            return ResponseEntity.badRequest()
                    .body(AccountResponseDto.builder()
                            .message("Un utilisateur existe déjà avec cette adresse email")
                            .build());
        }
        UserEntity newUser = UserEntity.builder()
                .firstName(user.getFirstname())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .lastName(user.getLastname())
                .build();
        RoleEntity role = roleJpaRepository.findByRoleName(RoleName.USER);
        newUser.setRoles(new ArrayList<>());
        newUser.getRoles().add(role);
        userJpaRepository.save(newUser);
        return ResponseEntity.ok(AccountResponseDto.builder().message("Utilisateur bien enregistré")
                .build());
    }

    public ResponseEntity<AccountResponseDto> login(LoginRequestDto request) {
        try {
            var user = userJpaRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            var jwtToken = jwtService.generateToken(user);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("Acces-Control-Expose-Headers", "Authorization");
            responseHeaders.add("Authorization", "Bearer " + jwtToken);

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(AccountResponseDto.builder()
                            .message("Utilisateur authentifié avec succès")
                            .token(jwtToken)
                            .user(globalMapper.mapUserEntityToUser(user))
                            .build());
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AccountResponseDto.builder()
                            .message("Utilisateur n'a pas été trouvé").build());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AccountResponseDto.builder()
                            .message("Erreur lors de la connexion").build());
        }
    }

}
