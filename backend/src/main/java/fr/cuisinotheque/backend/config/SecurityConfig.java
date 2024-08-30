
package fr.cuisinotheque.backend.config;


import fr.cuisinotheque.backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtAuthFilter filter;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
		.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))
				.authorizeHttpRequests(request -> request.requestMatchers("api/v1/recipes/*").permitAll())
				.authorizeHttpRequests(request -> request.requestMatchers("api/v1/account*").permitAll())
			.authorizeHttpRequests(request -> request.requestMatchers("api/v1/admin*").hasAuthority("ADMIN"))
			.authorizeHttpRequests(request -> request.requestMatchers("api/v1/user*").hasAuthority("USER"))

			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
			
		return http.build();
	}
	
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}

