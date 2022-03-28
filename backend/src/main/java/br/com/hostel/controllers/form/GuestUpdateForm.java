package br.com.hostel.controllers.form;

import java.time.LocalDate;

import br.com.hostel.models.Address;
import br.com.hostel.models.Guest;
import br.com.hostel.models.enums.Role;
import br.com.hostel.repositories.GuestRepository;
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
	
	public Guest updateGuestForm(Guest guest, GuestRepository guestRepository) {
		setParamIfIsNotNull(guest);
		return guestRepository.save(guest);
	}
	
	private void setParamIfIsNotNull(Guest guest) {
		
		if (title != null)
			guest.setTitle(title);
		
		if (name != null) 
			guest.setName(name);
		
		if (lastname != null) 
			guest.setLastName(lastname);
		
		if(birthday != null) 
			guest.setBirthday(birthday);
		
		if (address != null) 
			guest.setAddress(address);
		
		if(role != null)
			guest.setRole(role);
		
	}
}
