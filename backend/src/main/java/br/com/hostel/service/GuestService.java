package br.com.hostel.service;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.hostel.controller.form.GuestForm;
import br.com.hostel.controller.form.GuestUpdateForm;
import br.com.hostel.exceptions.guest.GuestException;
import br.com.hostel.exceptions.room.RoomException;
import br.com.hostel.model.Guest;
import br.com.hostel.repository.AddressRepository;
import br.com.hostel.repository.GuestRepository;

@Service
public class GuestService {

	public static final String GUEST_NOT_FOUND = "There isn't a guest with id = ";

	private final GuestRepository guestRepository;
	private final AddressRepository addressRepository;

	@Autowired
	public GuestService(GuestRepository guestRepository, AddressRepository addressRepository) {
		this.guestRepository = guestRepository;
		this.addressRepository = addressRepository;
	}

	public Guest createGuest(GuestForm form) throws RoomException {
		Optional<Guest> guestDB = guestRepository.findByEmail(form.getEmail());

		if (guestDB.isPresent()) {
			throw new GuestException("There is already a guest with e-mail = " + form.getEmail(),
					HttpStatus.BAD_REQUEST);
		}

		Guest guest = form.returnGuest(addressRepository);

		return guestRepository.save(guest);

	}

	public List<Guest> listAllGuests(String name) {

		if (name == null) {
			return guestRepository.findAll();
		}

		return guestRepository.findByName(name);
	}

	public Guest listOneGuest(Long id) throws RoomException {
		return guestRepository.findById(id)
				.orElseThrow(() -> new GuestException(GUEST_NOT_FOUND + id, HttpStatus.NOT_FOUND));

	}

	public Guest updateGuest(Long id, @Valid GuestUpdateForm form) {
		Guest guestDB = guestRepository.findById(id)
				.orElseThrow(() -> new GuestException(GUEST_NOT_FOUND + id, HttpStatus.NOT_FOUND));

		Guest guestToBeUpdated = form.updateGuestForm(guestDB, guestRepository);
		addressRepository.save(guestToBeUpdated.getAddress());

		return guestToBeUpdated;
	}

	public void deleteGuest(Long id) {

		Optional<Guest> guestOp = guestRepository.findById(id);

		if (guestOp.isEmpty()) {
			throw new GuestException(GUEST_NOT_FOUND + id, HttpStatus.NOT_FOUND);
		}

		guestRepository.deleteById(id);
	}
}
