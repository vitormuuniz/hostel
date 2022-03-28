package br.com.hostel.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.hostel.controllers.form.ReservationForm;
import br.com.hostel.controllers.form.ReservationUpdateForm;
import br.com.hostel.exceptions.guest.GuestException;
import br.com.hostel.exceptions.reservation.ReservationException;
import br.com.hostel.models.Guest;
import br.com.hostel.models.Reservation;
import br.com.hostel.repositories.GuestRepository;
import br.com.hostel.repositories.PaymentRepository;
import br.com.hostel.repositories.ReservationRepository;
import br.com.hostel.repositories.RoomRepository;

@Service
public class ReservationService {

	private static final String RESERVATION_NOT_FOUND = "There isn't a reservation with id = ";

	private final ReservationRepository reservationRepository;
	private final PaymentRepository paymentRepository;
	private final RoomRepository roomRepository;
	private final GuestRepository guestRepository;

	@Autowired
	public ReservationService(ReservationRepository reservationRepository, PaymentRepository paymentRepository, RoomRepository roomRepository, GuestRepository guestRepository) {
		this.reservationRepository = reservationRepository;
		this.paymentRepository = paymentRepository;
		this.roomRepository = roomRepository;
		this.guestRepository = guestRepository;
	}

	public Reservation registerReservation(ReservationForm form) {

		Reservation reservation = form.returnReservation(roomRepository);

		Guest guest = guestRepository.findById(reservation.getGuest_ID())
				.orElseThrow(() -> new GuestException("Guest ID haven't found", HttpStatus.NOT_FOUND));

		if (reservation.getRooms().isEmpty()) {
			throw new ReservationException("Rooms list cannot be empty", HttpStatus.BAD_REQUEST);
		}

		if (reservation.getCheckinDate().isBefore(LocalDate.now())
				|| reservation.getCheckoutDate().isBefore(reservation.getCheckinDate())) {
			throw new ReservationException("Verify your checkin/checkout date", HttpStatus.BAD_REQUEST);
		}

		reservation.setGuestName(guest.getName());
		
		paymentRepository.save(reservation.getPayment());
		reservationRepository.save(reservation);

		guest.addReservation(reservation);
		guestRepository.save(guest);

		return reservation;
	}

	public List<Reservation> listAllReservations(Long guestId) {

		if (guestId == null) {
			return reservationRepository.findAll();
		}

		Optional<Guest> guestOp = guestRepository.findById(guestId);

		return guestOp.map(guest -> new ArrayList<>(guest.getReservations())).orElseGet(ArrayList::new);
	}

	public Reservation listOneReservation(Long id) {
		return reservationRepository.findById(id)
				.orElseThrow(() -> new ReservationException(RESERVATION_NOT_FOUND + id, HttpStatus.NOT_FOUND));
	}

	public Reservation updateReservation(@PathVariable Long id, @RequestBody @Valid ReservationUpdateForm form) {

		Reservation reservationDB = reservationRepository.findById(id)
				.orElseThrow(() -> new ReservationException(RESERVATION_NOT_FOUND + id, HttpStatus.NOT_FOUND));

		Reservation reservationToBeUpdated = form.updateReservationForm(reservationDB, roomRepository);

		if (reservationToBeUpdated.getRooms().isEmpty())
			throw new ReservationException("Reservation rooms list cannot be empty", HttpStatus.BAD_REQUEST);

		roomRepository.saveAll(reservationToBeUpdated.getRooms());

		paymentRepository.save(reservationToBeUpdated.getPayment());

		return reservationToBeUpdated;
	}

	public void deleteReservation(Long id) {

		Optional<Reservation> reservationOp = reservationRepository.findById(id);

		if (reservationOp.isEmpty()) {
			throw new ReservationException(RESERVATION_NOT_FOUND + id, HttpStatus.NOT_FOUND);
		}

		reservationRepository.deleteById(id);

	}
}
