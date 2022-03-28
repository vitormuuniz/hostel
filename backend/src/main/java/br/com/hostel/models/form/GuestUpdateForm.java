package br.com.hostel.models.form;

import java.time.LocalDate;

import br.com.hostel.models.Address;
import br.com.hostel.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestUpdateForm {
	
	private String title;
	private String name;
	private String lastname; 
	private LocalDate birthday;
	private Address address;
	private Role role;
}
