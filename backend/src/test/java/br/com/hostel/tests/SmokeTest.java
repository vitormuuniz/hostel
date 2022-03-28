package br.com.hostel.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.hostel.controller.GuestController;
import br.com.hostel.controller.ReservationController;
import br.com.hostel.controller.RoomController;

@SpringBootTest
public class SmokeTest {

	@Autowired
	private GuestController guestController;
	
	@Autowired
	private ReservationController reservationController;
	
	@Autowired
	private RoomController roomController;

	@Test
	public void contextLoads() {
		assertThat(guestController).isNotNull();
		assertThat(reservationController).isNotNull();
		assertThat(roomController).isNotNull();
	}
}
