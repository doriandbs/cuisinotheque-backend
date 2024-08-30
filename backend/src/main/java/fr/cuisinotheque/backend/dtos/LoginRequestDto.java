package fr.cuisinotheque.backend.dtos;

import lombok.Data;

@Data
public class LoginRequestDto {
	private String email;
	private String password;
}
