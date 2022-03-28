package br.com.hostel.tests.unit.guest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.hostel.exceptions.GuestException;
import br.com.hostel.initializer.GuestsInitializer;
import br.com.hostel.models.Address;
import br.com.hostel.models.Guest;
import br.com.hostel.repositories.AddressRepository;
import br.com.hostel.repositories.GuestRepository;
import br.com.hostel.services.GuestService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GuestService.class)
class ListGuestsTest {

	@MockBean
	private GuestRepository guestRepository;

	@MockBean
	private AddressRepository addressRepository;

	@Autowired
	private GuestService guestService;
	
	private static final Address address = new Address();
	private static final Guest guest = new Guest();
	private static final Guest guest2 = new Guest();
	private static final List<Guest> guestsList = new ArrayList<>();

	@BeforeAll
	static void beforeAll() {
		
		GuestsInitializer.initialize(address, guest);
		GuestsInitializer.initialize(address, guest2);

		guest.setId(13L);
		guest2.setEmail("francisco@orkut.com");
		guest2.setName("Francisco");
		guest2.setLastName("Neto");
		guest2.setTitle("MR.");
		
		guestsList.add(guest);
		guestsList.add(guest2);
	}

	@Test
	void shouldReturnAllGuestsWithoutParamAndStatusOk() {

		when(guestRepository.findAll()).thenReturn(guestsList);
		
		List<Guest> listAllGuests = guestService.listAllGuests(null);
		
		assertEquals(guestsList.size(), listAllGuests.size());
	}
	
	@Test
	void shouldReturnOneGuestAndStatusOkByParam() {

		List<Guest> n = new ArrayList<>();
		n.add(guest2);
		
		when(guestRepository.findByName(guest2.getName())).thenReturn(n);
		
		List<Guest> justOneGuestList = guestService.listAllGuests(guest2.getName());
		
		assertEquals(1, justOneGuestList.size());
		assertEquals(guest2.getLastName(), justOneGuestList.get(0).getLastName());
	}
	
	@Test
	void shouldReturnEmptyListByUsingNonexistentName() {
		
		List<Guest> emptyList = new ArrayList<>();
		
		when(guestRepository.findByName(any())).thenReturn(emptyList);
		
		List<Guest> reqEmptyList = guestService.listAllGuests("nonexistent name");
		
		assertEquals(emptyList.size(), reqEmptyList.size());
	}
	
	@Test
	void shouldReturnOneGuestAndStatusOkByID() {

		Optional<Guest> opGuest = Optional.of(guest);
		
		when(guestRepository.findById(guest.getId())).thenReturn(opGuest);

		Guest reqGuest = guestService.listOneGuest(opGuest.get().getId());

		assertEquals(opGuest.get().getName(), reqGuest.getName());
		assertEquals(opGuest.get().getAddress().getCity(), reqGuest.getAddress().getCity());

	}
	
	@Test
	void shouldThrowExceptionByFindAGuestWithNonexistentID() {

		Optional<Guest> opGuest = Optional.empty();
		
		when(guestRepository.findById(guest.getId())).thenReturn(opGuest);

		GuestException thrown = 
				assertThrows(GuestException.class, 
					() -> guestService.listOneGuest(guest.getId()),
					"It was expected that listOneGuest() thrown an exception, " +
					"due to trying to find a guest with an nonexistent ID");

		assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());

	}
}
