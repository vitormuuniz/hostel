package br.com.hostel.tests.integration.reservation.delete;

import br.com.hostel.controllers.form.ReservationForm;
import br.com.hostel.initializer.ReservationInitializer;
import br.com.hostel.models.CheckPayment;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class DeleteReservationsTest {

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
	
	@BeforeEach
	void init() throws Exception {
		
		uri = new URI("/api/reservations/");
		
		ReservationInitializer.initialize(headers, reservationForm, checkPayment, rooms_ID, mockMvc, objectMapper);
	}
	
	@Test
	void shouldReturnNotFoundStatusWhenDeletingAReservationWithNonExistentID() throws Exception {

		paymentRepository.save(reservationForm.getPayment());
		reservationRepository.save(reservationForm.returnReservation(roomRepository));

		mockMvc
			.perform(delete(uri + "0")
			.headers(headers))
			.andDo(print())
			.andExpect(status().isNotFound());	
	}
	
	@Test
	void shouldAuthenticateAndDeleteOneReservationWithId1() throws Exception {

		paymentRepository.save(reservationForm.getPayment());
		reservationRepository.save(reservationForm.returnReservation(roomRepository));

		mockMvc
			.perform(delete(uri + "1")
			.headers(headers))
			.andDo(print())
			.andExpect(status().isOk());	
	}
}