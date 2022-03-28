package br.com.hostel.tests.integration.reservation;

import br.com.hostel.controllers.dto.ReservationDto;
import br.com.hostel.controllers.form.ReservationForm;
import br.com.hostel.initializer.ReservationInitializer;
import br.com.hostel.models.CashPayment;
import br.com.hostel.models.CheckPayment;
import br.com.hostel.models.CreditCardPayment;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class CreateReservationsTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private URI uri;
	private final HttpHeaders headers = new HttpHeaders();
	private final ReservationForm reservationForm = new ReservationForm();
	private final CheckPayment checkPayment = new CheckPayment();
	private final CashPayment cashPayment = new CashPayment();
	private final CreditCardPayment creditCardPayment = new CreditCardPayment();
	private final List<Long> rooms_ID = new ArrayList<>();
	
	@BeforeEach
	void init() throws Exception {
		
		uri = new URI("/api/reservations/");
		
		ReservationInitializer.initialize(headers, reservationForm, checkPayment, rooms_ID, mockMvc, objectMapper);
	}
	
	@Test
	void shouldReturnNotFoundStatusWithNonExistentGuestID() throws Exception {
		
		reservationForm.setGuest_ID(Long.MAX_VALUE);
		
		mockMvc.perform(post(uri)
				.headers(headers)
				.content(objectMapper.writeValueAsString(reservationForm)))
				.andDo(print())
				.andExpect(status().isNotFound());
	}
	
	@Test
	void shouldReturnBadRequestStatusWithReservationCheckinDateOlderActualDate() throws Exception {
		
		reservationForm.setCheckinDate(LocalDate.of(1900, 10, 10));
		
		mockMvc.perform(post(uri)
				.headers(headers)
				.content(objectMapper.writeValueAsString(reservationForm)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldReturnBadRequestWhenCheckoutDateIsOlderThanCheckinDate() throws Exception {
		
		reservationForm.setCheckinDate(LocalDate.of(2022, 10, 10));
		reservationForm.setCheckoutDate(LocalDate.of(2022, 10, 9));
		
		mockMvc.perform(post(uri)
				.headers(headers)
				.content(objectMapper.writeValueAsString(reservationForm)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldReturnBadRequestStatusWithReservationRoomsListEmpty() throws Exception {

		reservationForm.setRooms_ID(new ArrayList<>());
		
		mockMvc.perform(post(uri)
				.headers(headers)
				.content(objectMapper.writeValueAsString(reservationForm)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@Test
	void shouldCreateOneReservationUsingCheckPaymentSuccessfully() throws Exception {

		MvcResult result = 
				mockMvc
					.perform(post(uri)
					.headers(headers)
					.content(objectMapper.writeValueAsString(reservationForm)))
					.andDo(print())
					.andExpect(status().isCreated())
					.andReturn();
		
		String contentAsString = result.getResponse().getContentAsString();

		ReservationDto reservationObjResponse = objectMapper.readValue(contentAsString, ReservationDto.class);
		
		CheckPayment checkObjResponse = (CheckPayment) reservationObjResponse.getPayment();

		assertEquals(reservationForm.getCheckinDate(), reservationObjResponse.getCheckinDate());
		assertEquals(checkPayment.getAmount(), reservationObjResponse.getPayment().getAmount());
		assertEquals(checkPayment.getBankName(), checkObjResponse.getBankName());
	}
	
	@Test
	void shouldCreateOneReservationUsingCashPaymentSuccessfully() throws Exception {
		cashPayment.setAmount(4000);
		cashPayment.setAmountTendered(10000);
		cashPayment.setDate(LocalDateTime.of(LocalDate.of(2025,1,25), LocalTime.of(21, 32)));
		
		reservationForm.setPayment(cashPayment);
		
		rooms_ID.add(3L);
		reservationForm.setRooms_ID(rooms_ID);
		
		MvcResult result = 
				mockMvc
					.perform(post(uri)
					.headers(headers)
					.content(objectMapper.writeValueAsString(reservationForm)))
					.andDo(print())
					.andExpect(status().isCreated())
					.andReturn();

		String contentAsString = result.getResponse().getContentAsString();

		ReservationDto reservationObjResponse = objectMapper.readValue(contentAsString, ReservationDto.class);
		
		CashPayment cashObjResponse = (CashPayment) reservationObjResponse.getPayment();

		assertEquals(reservationForm.getCheckinDate(), reservationObjResponse.getCheckinDate());
		assertEquals(cashPayment.getAmount(), cashObjResponse.getAmount());
	}
	
	@Test
	void shouldCreateOneReservationUsingCreditCardPaymentSuccessfully() throws Exception {
		creditCardPayment.setAmount(5000);
		creditCardPayment.setDate(LocalDateTime.of(LocalDate.of(2025,1,25), LocalTime.of(21, 33)));
		creditCardPayment.setIssuer("VISA");
		creditCardPayment.setNameOnCard("MARIA A SILVA");
		creditCardPayment.setCardNumber("1234 5678 9101 1121");
		creditCardPayment.setExpirationDate(LocalDate.of(2048, 5, 1));
		creditCardPayment.setSecurityCode("123");
		
		reservationForm.setPayment(creditCardPayment);
		
		rooms_ID.add(4L);
		rooms_ID.add(5L);
		reservationForm.setRooms_ID(rooms_ID);
		
		MvcResult result = 
				mockMvc
				.perform(post(uri)
					.headers(headers)
					.content(objectMapper.writeValueAsString(reservationForm)))
					.andDo(print())
					.andExpect(status().isCreated())
					.andReturn();
		
		String contentAsString = result.getResponse().getContentAsString();
		
		ReservationDto reservationObjResponse = objectMapper.readValue(contentAsString, ReservationDto.class);
		
		CreditCardPayment creditCardObjResponse = (CreditCardPayment) reservationObjResponse.getPayment();
		
		assertEquals(reservationForm.getCheckinDate(), reservationObjResponse.getCheckinDate());
		assertEquals(creditCardPayment.getAmount(), creditCardObjResponse.getAmount());
		assertEquals(creditCardPayment.getNameOnCard(), creditCardObjResponse.getNameOnCard());
	}
}
