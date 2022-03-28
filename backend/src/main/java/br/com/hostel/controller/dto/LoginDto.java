package br.com.hostel.controller.dto;

import br.com.hostel.model.enums.Role;
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
