package br.com.hostel.models.form;

import br.com.hostel.models.DailyRate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomUpdateForm {
	
	private String description;
	private int number;
	private double dimension;
	private DailyRate dailyRate;
}
