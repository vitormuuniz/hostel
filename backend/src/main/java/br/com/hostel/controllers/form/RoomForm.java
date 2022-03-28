package br.com.hostel.controllers.form;

import javax.validation.constraints.NotNull;

import br.com.hostel.models.DailyRate;
import br.com.hostel.models.Room;
import br.com.hostel.repositories.DailyRateRepository;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomForm {

	@NotNull
	private String description;
	
	@NotNull
	private Integer number;
	
	@NotNull
	private Double dimension;
	
	@NotNull
	private Integer maxNumberOfGuests;
	
	@NotNull
	private	DailyRate dailyRate;

	public Room returnRoom(DailyRateRepository dailyRateRepository) {
		dailyRateRepository.save(dailyRate);
		
		return new Room(description, number, dimension, maxNumberOfGuests, dailyRate);
	}

}
