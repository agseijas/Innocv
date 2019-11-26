package es.innocv.uidex.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import es.innocv.uidex.exception.UserFoundException;
import es.innocv.uidex.exception.UserNotFoundException;
import es.innocv.uidex.utils.error.CustomApiErrorMessage;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(value = UserNotFoundException.class)
	public ResponseEntity<CustomApiErrorMessage> handleUserNotFoundException(UserNotFoundException ex) {
		return createResponseEntity(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), null);
	}
	
	@ExceptionHandler(value = UserFoundException.class)
	public ResponseEntity<CustomApiErrorMessage> handleUserFoundException(UserFoundException ex) {
		return createResponseEntity(HttpStatus.CONFLICT, ex.getLocalizedMessage(), null);
	}
	
	@ExceptionHandler(value = InvalidFormatException.class)
	public ResponseEntity<CustomApiErrorMessage> handleInvalidFormatException(InvalidFormatException ex) {
		StringBuffer errorMessage = new StringBuffer("Parameter error: ");
		errorMessage.append(ex.getPath().get(0).getFieldName());
		errorMessage.append(", value: " + ex.getValue());
		
		return createResponseEntity(HttpStatus.BAD_REQUEST, errorMessage.toString(), ex.getLocalizedMessage());
	}
	
	private ResponseEntity<CustomApiErrorMessage> createResponseEntity(HttpStatus status, String message, String debugMessage) {
		logger.error(status.toString() + " - " + message + " - " + debugMessage);
		
		return ResponseEntity
				.status(status)
				.body(new CustomApiErrorMessage
						.CustomApiErrorMessageBuilder()
						.setStatus(status)
						.setError(message)
						.setDebugMessage(debugMessage)
						.build()
						);
	}
}
