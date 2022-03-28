package br.com.hostel.tests.unit.room;

import br.com.hostel.controller.form.RoomForm;
import br.com.hostel.exceptions.room.RoomException;
import br.com.hostel.initializer.RoomInitializer;
import br.com.hostel.model.DailyRate;
import br.com.hostel.model.Room;
import br.com.hostel.repository.DailyRateRepository;
import br.com.hostel.repository.ReservationRepository;
import br.com.hostel.repository.RoomRepository;
import br.com.hostel.service.RoomService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RoomService.class)
class CreateRoomsTest {

	@MockBean
	private RoomRepository roomRepository;

	@MockBean
	private DailyRateRepository dailyRepository;

	@MockBean
	private ReservationRepository reservationRepository;

	@MockBean
	private RoomForm form;

	@Autowired
	private RoomService service;

	private static final Room room = new Room();
	private static final DailyRate dailyRate = new DailyRate();

	@BeforeAll
	static void beforeAll() {
		RoomInitializer.initialize(room, dailyRate);
	}

	@Test
	void shouldCreateOneRoomSuccessfully() throws RoomException {

		Optional<Room> nonexistentRoom = Optional.empty();
		
		when(form.returnRoom(any())).thenReturn(room);
		when(roomRepository.findByNumber(any())).thenReturn(nonexistentRoom);
		when(roomRepository.save(any())).thenReturn(room);

		Room reqRoom = service.registerRoom(form);

		assertEquals(room.getDescription(), reqRoom.getDescription());
		assertEquals(room.getDimension(), reqRoom.getDimension());

	}
	
	@Test
	void shouldReturnNullWithExistentRoomNumber() throws RoomException {
		
		Optional<Room> opRoom = Optional.of(room);

		when(form.returnRoom(any())).thenReturn(room);
		when(roomRepository.findByNumber(any())).thenReturn(opRoom);
		
		RoomException thrown = 
				assertThrows(RoomException.class, 
					() -> service.registerRoom(form),
					"It was expected that registerRoom() thrown an exception, " +
					"due to trying to create a room with an existing number");

		assertEquals(HttpStatus.BAD_REQUEST, thrown.getHttpStatus());
	}
}
