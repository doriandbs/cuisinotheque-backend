package fr.cuisinotheque.backend.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponseDto {
	private String message;
}
