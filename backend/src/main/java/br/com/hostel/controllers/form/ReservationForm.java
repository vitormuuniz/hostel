package br.com.hostel.controllers.form;

import br.com.hostel.models.Payment;
import br.com.hostel.models.Reservation;
import br.com.hostel.models.Room;
import br.com.hostel.repositories.RoomRepository;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ReservationForm {

	@NotNull
	private Long guest_ID;
	@NotNull
	private String guestName;
	@NotNull
    private int numberOfGuests;
	@NotNull
	private LocalDate checkinDate;
	@NotNull
	private LocalDate checkoutDate;
	@NotNull
	private List<Long> rooms_ID = new ArrayList<>();
	@NotNull
	private Payment payment;

	public Reservation returnReservation(RoomRepository roomRepository) {
		
		payment.setDate(LocalDateTime.now());
		
		Set<Room> roomsList = new HashSet<>();

		rooms_ID.forEach(id -> roomRepository.findById(id).ifPresent(roomsList::add));

		return new Reservation(guest_ID, guestName, numberOfGuests, LocalDate.now(), checkinDate, checkoutDate, 
				roomsList, payment);
	}

}
