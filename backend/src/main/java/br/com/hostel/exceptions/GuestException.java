package br.com.hostel.exceptions;

import org.springframework.http.HttpStatus;

public class GuestException extends BaseException {

	public GuestException(String message, HttpStatus status) {
		super(message, status);
	}
}
