package br.com.hostel.models.dto;

import br.com.hostel.enums.Role;
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
