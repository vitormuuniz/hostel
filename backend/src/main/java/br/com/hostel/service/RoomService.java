package br.com.hostel.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.hostel.controller.form.RoomForm;
import br.com.hostel.controller.form.RoomUpdateForm;
import br.com.hostel.controller.helper.RoomFilter;
import br.com.hostel.exceptions.room.RoomException;
import br.com.hostel.model.Reservation;
import br.com.hostel.model.Room;
import br.com.hostel.repository.DailyRateRepository;
import br.com.hostel.repository.ReservationRepository;
import br.com.hostel.repository.RoomRepository;

@Service
public class RoomService {

	private static final String ROOM_NOT_FOUND = "Room ID haven't found";

	private final RoomRepository roomRepository;
	private final DailyRateRepository dailyRateRepository;
	private final ReservationRepository reservationRepository;

	@Autowired
	public RoomService(RoomRepository roomRepository, DailyRateRepository dailyRateRepository, ReservationRepository reservationRepository) {
		this.roomRepository = roomRepository;
		this.dailyRateRepository = dailyRateRepository;
		this.reservationRepository = reservationRepository;
	}

	public Room registerRoom(RoomForm form) throws RoomException {

		Room room = form.returnRoom(dailyRateRepository);
		Optional<Room> roomOp = roomRepository.findByNumber(room.getNumber());

		if (roomOp.isPresent()) {
			throw new RoomException("There is already a room with number = " + room.getNumber(), HttpStatus.BAD_REQUEST);
		}

		return roomRepository.save(room);
	}

	public List<Room> listAllRooms(RoomFilter roomFilter) {

		List<Room> unavailableRooms = new ArrayList<>();
		List<Room> availableRooms = roomRepository.findAll();

		verifyValidRooms(roomFilter, unavailableRooms, availableRooms);

		if (roomFilter.getCheckinDate() != null && roomFilter.getCheckoutDate() != null) {
			LocalDate checkinDate = LocalDate.parse(roomFilter.getCheckinDate());
			LocalDate checkoutDate = LocalDate.parse(roomFilter.getCheckoutDate());

			List<Reservation> reservationsList = reservationRepository.findAll();

			reservationsList.forEach(reservation ->
					verifyValidRoomsWithinAPeriod(unavailableRooms, checkinDate, checkoutDate, reservation));

			availableRooms.removeAll(unavailableRooms);
		}

		return availableRooms;
	}

	private void verifyValidRooms(RoomFilter roomFilter, List<Room> unavailableRooms, List<Room> availableRooms) {

		if (roomFilter.getMinDailyRate() != null) {
			unavailableRooms.addAll(
					availableRooms
							.stream()
							.filter(room -> room.getDailyRate().getPrice() < roomFilter.getMinDailyRate())
							.collect(Collectors.toList())
			);
		}


		if (roomFilter.getMaxDailyRate() != null) {
			unavailableRooms.addAll(
					availableRooms
							.stream()
							.filter(room -> room.getDailyRate().getPrice() > roomFilter.getMaxDailyRate())
							.collect(Collectors.toList())
			);
		}

		if (roomFilter.getNumberOfGuests() != null) {
			unavailableRooms.addAll(
					availableRooms
							.stream()
							.filter(room -> room.getMaxNumberOfGuests() < roomFilter.getNumberOfGuests())
							.collect(Collectors.toList())
			);
		}

		availableRooms.removeAll(unavailableRooms);
	}

	private void verifyValidRoomsWithinAPeriod(List<Room> unavailableRooms, LocalDate checkinDate,
											   LocalDate checkoutDate, Reservation reservation) {
		long numOfDays = ChronoUnit.DAYS.between(reservation.getCheckinDate(), reservation.getCheckoutDate());

		List<LocalDate> dates = Stream.iterate(reservation.getCheckinDate(), date -> date.plusDays(1))
				.limit(numOfDays)
				.collect(Collectors.toList());

		if ((dates.contains(checkinDate) || dates.contains(checkoutDate))
				|| (checkinDate.isBefore(reservation.getCheckinDate()) && checkoutDate.isAfter(reservation.getCheckoutDate())
				|| checkoutDate.isEqual(reservation.getCheckoutDate()))) {

			unavailableRooms.addAll(
					reservation.getRooms()
							.stream()
							.filter(room -> !unavailableRooms.contains(room))
							.collect(Collectors.toList())
			);
		}
	}

	public Room listOneRoom(Long id) throws RoomException {
		return roomRepository.findById(id).orElseThrow(() -> new RoomException(ROOM_NOT_FOUND, HttpStatus.NOT_FOUND));
	}

	public Room updateRoom(@PathVariable Long id, @RequestBody @Valid RoomUpdateForm form) throws RoomException {

		Room roomDB = roomRepository.findById(id).orElseThrow(() -> new RoomException(ROOM_NOT_FOUND, HttpStatus.NOT_FOUND));

		Room roomToBeUpdated = form.updateRoomForm(roomDB, roomRepository);

		dailyRateRepository.save(roomToBeUpdated.getDailyRate());

		roomRepository.save(roomToBeUpdated);
		
		return roomToBeUpdated;
	}

	public void deleteRoom(Long id) throws RoomException {
		Optional<Room> roomOp = roomRepository.findById(id);

		if (roomOp.isEmpty()) {
			throw new RoomException(ROOM_NOT_FOUND, HttpStatus.NOT_FOUND);
		}

		roomRepository.deleteById(id);
	}
}
