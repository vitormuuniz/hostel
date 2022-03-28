package br.com.hostel.controllers.dto;

import br.com.hostel.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDto {

	private String token;
	private String type;
	private Long guest_ID;
	private Role role;

}
