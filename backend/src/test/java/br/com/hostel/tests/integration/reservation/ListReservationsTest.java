package br.com.hostel.tests.integration.reservation;

import br.com.hostel.models.dto.ReservationDto;
import br.com.hostel.models.form.ReservationForm;
import br.com.hostel.initializer.ReservationInitializer;
import br.com.hostel.models.CheckPayment;
import br.com.hostel.models.Guest;
import br.com.hostel.models.Reservation;
import br.com.hostel.repositories.GuestRepository;
import br.com.hostel.repositories.PaymentRepository;
import br.com.hostel.repositories.ReservationRepository;
import br.com.hostel.repositories.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ListReservationsTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private static URI uri;
	private static final HttpHeaders headers = new HttpHeaders();
	private static Reservation reservation1, reservation2;
	private static final CheckPayment checkPayment = new CheckPayment();
	private static final ReservationForm reservationForm = new ReservationForm();
	private static Guest guest = new Guest();
	private static final List<Long> rooms_ID = new ArrayList<>();
	private static final Set<Reservation> reservationsList = new HashSet<>();
	
	@BeforeAll
    static void beforeAll(@Autowired ReservationRepository reservationRepository, 
    		@Autowired PaymentRepository paymentRepository, @Autowired GuestRepository guestRepository, 
    		@Autowired RoomRepository roomRepository, @Autowired MockMvc mockMvc, 
    		@Autowired ObjectMapper objectMapper) throws Exception {
		
		uri = new URI("/api/reservations/");
		
		ReservationInitializer.initialize(headers, reservationForm, checkPayment, rooms_ID, mockMvc, objectMapper);
		
		paymentRepository.save(reservationForm.getPayment());
		
		reservation1 = reservationRepository.save(reservationForm.returnReservation(roomRepository));
		reservationsList.add(reservation1);

		reservationForm.setCheckinDate(LocalDate.of(2025, 5, 1));
		reservationForm.setCheckoutDate(LocalDate.of(2025, 5, 4));
		
		rooms_ID.remove(2L);
		rooms_ID.add(3L);
		
		reservation2 = reservationRepository.save(reservationForm.returnReservation(roomRepository));
		reservationsList.add(reservation2);
		
		guestRepository.findById(reservationForm.getGuest_ID()).ifPresent(guest -> {
			guest.setReservations(reservationsList);
			ListReservationsTest.guest = guestRepository.save(guest);
		});
	}
	
	@Test
	void shouldReturnAllReservationsWithoutParam() throws Exception {

		MvcResult result = 
				mockMvc.perform(get(uri)
						.headers(headers))
						.andDo(print())
						.andExpect(status().isOk())
						.andReturn();

		String contentAsString = result.getResponse().getContentAsString();

		ReservationDto[] reservationObjResponse = objectMapper.readValue(contentAsString, ReservationDto[].class);

		assertEquals(reservationsList.size(), reservationObjResponse.length);
	}
	
	@Test
	void shouldReturnAllReservationsByGuestId() throws Exception {

		MvcResult result = 
				mockMvc.perform(get(uri)
						.param("guestId", guest.getId().toString())
						.headers(headers))
						.andDo(print())
						.andReturn();

		String contentAsString = result.getResponse().getContentAsString();

		ReservationDto[] reservationObjResponse = objectMapper.readValue(contentAsString, ReservationDto[].class);

		assertEquals(reservationsList.size(), reservationObjResponse.length);
		assertEquals(reservation1.getGuestName(), guest.getName());
		assertEquals(reservation2.getGuestName(), guest.getName());
	}
	
	@Test
	void shouldReturnOneReservationById() throws Exception {

		MvcResult result = 
				mockMvc.perform(get(uri  + "1")
						.headers(headers))
						.andDo(print())
						.andExpect(status().isOk())
						.andReturn();

		String contentAsString = result.getResponse().getContentAsString();

		ReservationDto reservationObjResponse = objectMapper.readValue(contentAsString, ReservationDto.class);
		
		assertEquals(reservation1.getCheckinDate(), reservationObjResponse.getCheckinDate());
		assertEquals(reservation1.getCheckoutDate(), reservationObjResponse.getCheckoutDate());
	}
	
	@Test
	void shouldReturnNotFoundStatusWithNonExistentReservationsId() throws Exception {

		mockMvc.perform(get(uri  + String.valueOf(Long.MAX_VALUE))
				.headers(headers))
				.andDo(print())
				.andExpect(status().isNotFound())
				.andReturn();
	}
	
	@Test
	void shouldReturnNotFoundStatusAndNullBodyByUsingWrongParam() throws Exception {

		MvcResult result = 
				mockMvc.perform(get(uri)
						.param("guestId", String.valueOf(Long.MAX_VALUE))
						.headers(headers))
						.andDo(print())
						.andReturn();
		
		String contentAsString = result.getResponse().getContentAsString();

		ReservationDto[] reservationObjResponse = objectMapper.readValue(contentAsString, ReservationDto[].class);
		
		assertEquals(0, reservationObjResponse.length);
	}
}
