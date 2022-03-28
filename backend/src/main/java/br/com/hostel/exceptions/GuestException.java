package br.com.hostel.exceptions;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ser.Serializers.Base;

import br.com.hostel.exceptions.BaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class GuestException extends BaseException {

	public GuestException(String message, HttpStatus status) {
		super(message, status);
	}
}
