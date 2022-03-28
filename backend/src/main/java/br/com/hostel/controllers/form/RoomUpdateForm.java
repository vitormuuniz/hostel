package br.com.hostel.controllers.form;

import java.util.Optional;

import br.com.hostel.models.DailyRate;
import br.com.hostel.models.Room;
import br.com.hostel.repositories.RoomRepository;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomUpdateForm {
	
	private String description;
	private int number;
	private double dimension;
	private DailyRate dailyRate;
	
	public Room updateRoomForm(Room room, RoomRepository roomRepository) {
		setParamIfIsNotNull(room, roomRepository);
		
		return room;
	}
	
	private void setParamIfIsNotNull(Room room, RoomRepository roomRepository) {
		
		if (description != null)
			room.setDescription(description);

		if (number != 0) {
			Optional<Room> roomOp = roomRepository.findByNumber(number);

			if (roomOp.isEmpty()) {
				room.setNumber(number);
			}
		}

		if (dimension != 0)
			room.setDimension(dimension);
		
		if (dailyRate != null)
			room.setDailyRate(dailyRate);
	}
}
