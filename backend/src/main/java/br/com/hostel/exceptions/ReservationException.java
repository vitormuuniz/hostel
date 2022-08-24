package br.com.hostel.exceptions;

import org.springframework.http.HttpStatus;

public class ReservationException extends BaseException {

	public ReservationException(String message, HttpStatus status) {
		super(message, status);
	}
}
