package br.com.hostel.models.form;

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
}
