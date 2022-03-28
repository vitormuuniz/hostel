package br.com.hostel.controllers.form;

import br.com.hostel.models.Payment;
import br.com.hostel.models.Reservation;
import br.com.hostel.models.Room;
import br.com.hostel.repositories.RoomRepository;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ReservationUpdateForm {
	
	private int numberOfGuests;
	private LocalDate reservationDate;
	private LocalDate checkinDate;
	private LocalDate checkoutDate;
	
	private List<Long> rooms_ID = new ArrayList<>();	
	
	private Payment payment;
	
	public Reservation updateReservationForm(Reservation reservation, RoomRepository roomRepository) {
		setParamIfIsNotNull(reservation, roomRepository);
		
		return reservation;
	}
	
	private void setParamIfIsNotNull(Reservation reservation, RoomRepository roomRepository) {
		
		if (numberOfGuests != 0)
			reservation.setNumberOfGuests(numberOfGuests);
		
		if (reservationDate != null)
			reservation.setReservationDate(reservationDate);
		
		if (checkinDate != null)
			reservation.setCheckinDate(checkinDate);
		
		if (checkoutDate != null)
			reservation.setCheckoutDate(checkoutDate);

		if (rooms_ID != null) {
			Set<Room> roomsList = new HashSet<>();

			rooms_ID.forEach(id -> roomRepository.findById(id).ifPresent(roomsList::add));

			reservation.setRooms(roomsList);
		}
		
		if (payment != null) {
			payment.setDate(LocalDateTime.now());
			reservation.setPayment(payment);
		}
	}
}
