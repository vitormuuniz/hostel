package br.com.hostel.tests.integration.reservation;

import br.com.hostel.models.dto.ReservationDto;
import br.com.hostel.models.form.ReservationForm;
import br.com.hostel.models.form.ReservationUpdateForm;
import br.com.hostel.initializer.ReservationInitializer;
import br.com.hostel.models.CheckPayment;
import br.com.hostel.models.Reservation;
import br.com.hostel.repositories.PaymentRepository;
import br.com.hostel.repositories.ReservationRepository;
import br.com.hostel.repositories.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class UpdateReservationsTest {

	@Autowired
	private ReservationRepository reservationRepository;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private URI uri;
	private final HttpHeaders headers = new HttpHeaders();
	private final ReservationForm reservationForm = new ReservationForm();
	private final CheckPayment checkPayment = new CheckPayment();
	private final List<Long> rooms_ID = new ArrayList<>();
	private Reservation reservation;
	
	@BeforeEach
	public void init() throws Exception {
		
		uri = new URI("/api/reservations/");
		
		ReservationInitializer.initialize(headers, reservationForm, checkPayment, rooms_ID, mockMvc, objectMapper);
		
		paymentRepository.save(checkPayment);
		reservation = reservationRepository.save(reservationForm.returnReservation(roomRepository));
	}
	
	@Test
	void shouldAuthenticateAndUpdateReservationInformation() throws Exception {

		ReservationUpdateForm rsvToUpdate = new ReservationUpdateForm();
		rsvToUpdate.setNumberOfGuests(3);
		rsvToUpdate.setPayment(reservation.getPayment());
		rsvToUpdate.getPayment().setAmount(5500);
		rsvToUpdate.setRooms_ID(rooms_ID);
		
		MvcResult result = 
				mockMvc
					.perform(put(uri + reservation.getId().toString())
					.headers(headers)
					.content(objectMapper.writeValueAsString(rsvToUpdate)))
					.andDo(print())
					.andExpect(status().isOk())
					.andReturn();
		
		String contentAsString = result.getResponse().getContentAsString();

		ReservationDto reservationObjResponse = objectMapper.readValue(contentAsString, ReservationDto.class);
		
		assertEquals(reservationObjResponse.getCheckinDate(), reservation.getCheckinDate());
		assertEquals(reservationObjResponse.getPayment().getAmount(), rsvToUpdate.getPayment().getAmount());
		assertEquals(rsvToUpdate.getRooms_ID().size(), reservationObjResponse.getRooms().size());
	}
	
	@Test
	void shouldReturnNotFoundStatusWhenUpdateWithNonExistentID() throws Exception {
		
		ReservationUpdateForm rsvToUpdate = new ReservationUpdateForm();
		rsvToUpdate.setNumberOfGuests(3);
		rsvToUpdate.setPayment(reservation.getPayment());
		rsvToUpdate.getPayment().setAmount(5500);
		rsvToUpdate.setRooms_ID(rooms_ID);
		
		mockMvc
			.perform(put(uri + "999")
				.headers(headers)
				.content(objectMapper.writeValueAsString(rsvToUpdate)))
				.andDo(print())
				.andExpect(status().isNotFound())
				.andReturn();
	}
	
	@Test
	void shouldReturnBadRequestStatusWhenUpdateWithEmptyReservationRoomsList() throws Exception {
		
		ReservationUpdateForm rsvToUpdate = new ReservationUpdateForm();
		rsvToUpdate.setNumberOfGuests(3);
		rsvToUpdate.setPayment(reservation.getPayment());
		rsvToUpdate.getPayment().setAmount(5500);
		rsvToUpdate.setRooms_ID(Collections.singletonList(Long.MAX_VALUE));
		
		reservation.setRooms(null);
		
		reservationRepository.save(reservation);
		
		mockMvc
			.perform(put(uri + reservation.getId().toString())
				.headers(headers)
				.content(objectMapper.writeValueAsString(rsvToUpdate)))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn();
	}
}
