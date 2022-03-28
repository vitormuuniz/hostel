package br.com.hostel.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import org.springframework.http.HttpStatus;

import br.com.hostel.exceptions.ReservationException;
import br.com.hostel.models.form.ReservationUpdateForm;
import br.com.hostel.repositories.RoomRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Reservation implements Comparable<Reservation>{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
	private Long guest_ID;
	@Column(nullable=false)
	private String guestName;
	@Column(nullable=false)
	private int numberOfGuests;
	@Column(nullable=false)
	private LocalDate reservationDate;
	@Column(nullable=false)
	private LocalDate checkinDate;
	@Column(nullable=false)
	private LocalDate checkoutDate;
	
	@ManyToMany
	@Column(nullable=false)
	private Set<Room> rooms = new HashSet<>();
	
	@OneToOne(cascade=CascadeType.REMOVE)
	@JoinColumn(name = "payment_ID", nullable = false)
	private Payment payment;
	
	public Reservation(Long guest_ID, String guestName, int numberOfGuests,LocalDate reservationDate, LocalDate checkinDate, LocalDate checkoutDate, Set<Room> rooms, Payment payment) {
		this.guest_ID = guest_ID;
		this.guestName = guestName;
		this.numberOfGuests = numberOfGuests;
		this.reservationDate = reservationDate;
		this.checkinDate = checkinDate;
		this.checkoutDate = checkoutDate;
		this.rooms = rooms;
		this.payment = payment;
	}

	@Override
	public int compareTo(Reservation otherReservation) {
		return this.getId().compareTo(otherReservation.getId());
	}

	public void setParamIfIsNotNull(ReservationUpdateForm form, RoomRepository roomRepository) {

		if (form.getNumberOfGuests() != 0)
			setNumberOfGuests(form.getNumberOfGuests());

		if (form.getReservationDate() != null)
			setReservationDate(form.getReservationDate());

		if (form.getCheckinDate() != null)
			setCheckinDate(form.getCheckinDate());

		if (form.getCheckoutDate() != null)
			setCheckoutDate(form.getCheckoutDate());

		if (form.getRooms_ID() != null) {
			Set<Room> roomsList = new HashSet<>();

			form.getRooms_ID().forEach(roomId -> roomRepository.findById(roomId).ifPresent(roomsList::add));

			if (roomsList.isEmpty()) {
				throw new ReservationException("Reservation rooms list cannot be empty", HttpStatus.BAD_REQUEST);
			}
			setRooms(roomsList);
		}

		if (form.getPayment() != null) {
			form.getPayment().setDate(LocalDateTime.now());
			setPayment(form.getPayment());
		}
	}
}
