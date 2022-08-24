package br.com.hostel.models.form;

import br.com.hostel.models.Payment;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

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
