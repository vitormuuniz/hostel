package br.com.hostel.models;

import java.util.Objects;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.springframework.util.StringUtils;

import br.com.hostel.models.form.RoomUpdateForm;
import br.com.hostel.repositories.RoomRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Room {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String description;
	@Column(nullable = false)
	private Integer number;
	@Column(nullable = false)
	private Double dimension;
	@Column(nullable = false)
	private Integer maxNumberOfGuests;

	@OneToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "dailyRate_ID", nullable = false)
	private DailyRate dailyRate;

	public Room(String description, Integer number, Double dimension, Integer maxNumberOfGuests, DailyRate dailyRate) {
		this.description = description;
		this.number = number;
		this.dimension = dimension;
		this.maxNumberOfGuests = maxNumberOfGuests;
		this.dailyRate = dailyRate;
	}

	public void setParamsIfIsNonNullOrEmpty(RoomUpdateForm form, RoomRepository roomRepository) {

		if (!StringUtils.isEmpty(form.getDescription())) {
			setDescription(form.getDescription());
		}

		if (form.getNumber() != 0) {
			Optional<Room> roomOp = roomRepository.findByNumber(form.getNumber());

			if (roomOp.isEmpty()) {
				setNumber(form.getNumber());
			}
		}

		if (form.getDimension() != 0) {
			setDimension(form.getDimension());
		}

		if (Objects.nonNull(form.getDailyRate())) {
			setDailyRate(form.getDailyRate());
		}
	}
}
