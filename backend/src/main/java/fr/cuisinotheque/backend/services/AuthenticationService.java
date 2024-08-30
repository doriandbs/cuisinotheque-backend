
package fr.cuisinotheque.backend.services;

import fr.cuisinotheque.backend.dtos.AccountResponseDto;
import fr.cuisinotheque.backend.dtos.LoginRequestDto;
import fr.cuisinotheque.backend.dtos.RegisterRequestDto;
import fr.cuisinotheque.backend.security.CustomPasswordEncoder;
import fr.cuisinotheque.data.entities.UserEntity;
import fr.cuisinotheque.data.repositories.IUserJpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final IUserJpaRepository userJpaRepository;
    
    private final CustomPasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    
    
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
    	userJpaRepository.save(newUser);
    	return ResponseEntity.ok(AccountResponseDto.builder().message("Utilisateur bien enregistré")
    			.build());
    }

	public ResponseEntity<AccountResponseDto> login(LoginRequestDto request) {
		var user = userJpaRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		var jwtToken = jwtService.generateToken(user);
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Acces-Control-Expose-Headers", "Authorization");
		responseHeaders.add("Authorization", "Bearer " + jwtToken);
		System.out.println(jwtToken);
		
		return ResponseEntity.ok()
				.headers(responseHeaders)
				.body(AccountResponseDto.builder()
						.message("Utilisateur authentifié avec succès").build());
	}
}
