package br.com.hostel.initializer;

import java.time.LocalDate;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.hostel.models.Address;
import br.com.hostel.models.Guest;
import br.com.hostel.models.enums.Role;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class GuestsInitializer {
	
	public static void initialize(Address address, Guest guest) {
		
		address.setAddressName("Rua Governador Valadares");
		address.setCity("Amparo");
		address.setCountry("Brasil");
		address.setState("SP");
		address.setZipCode("13900-000");
		
		guest.setAddress(address);
		guest.setBirthday(LocalDate.of(1900, 12, 12));
		guest.setEmail("washington@orkut.com");
		guest.setName("Washington");
		guest.setLastName("Ferrolho");
		guest.setTitle("MR.");
		guest.setPassword("1234567");
		guest.setRole(Role.ROLE_USER);
	}
}
