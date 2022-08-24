package br.com.hostel.exceptions;

import org.springframework.http.HttpStatus;

public class RoomException extends BaseException {

	public RoomException(String message, HttpStatus status) {
		super(message, status);
	}
}
