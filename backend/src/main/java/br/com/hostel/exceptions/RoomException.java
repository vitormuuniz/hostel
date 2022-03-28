package br.com.hostel.exceptions;

import org.springframework.http.HttpStatus;

import br.com.hostel.exceptions.BaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class RoomException extends BaseException {

	public RoomException(String message, HttpStatus status) {
		super(message, status);
	}
}
