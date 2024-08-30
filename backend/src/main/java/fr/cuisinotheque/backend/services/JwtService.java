
package fr.cuisinotheque.backend.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.lang.Function;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	@Value("${spring.jwt.security.key}")
	private String secretKey;
	

	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		List<String> roles = new ArrayList<>();

		for (GrantedAuthority authority : userDetails.getAuthorities()) {
			roles.add(authority.getAuthority());
		}

		claims.put("roles", roles);
		claims.putAll(extraClaims);
		return Jwts.builder().claims(claims).subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)).signWith(getSignInKey()).compact();
	}

	private SecretKey getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		
		return claimsResolver.apply(claims);
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSignInKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
	
	public String extractUsername(String jwt) {
		return extractClaim(jwt, Claims::getSubject);
	}
	
	private Date extractExpiration(String jwt) {
		return extractClaim(jwt, Claims::getExpiration);
	}
	
	public boolean isTokenValid(String jwt, UserDetails userDetails) {
		final String username = extractUsername(jwt);
		return (username.equals(userDetails.getUsername()) && !isTokenExpiration(jwt));
	}
	
	private boolean isTokenExpiration(String jwt) {
		return extractExpiration(jwt).before(new Date());
	}
}
