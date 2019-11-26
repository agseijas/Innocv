package es.innocv.uidex.exception;

public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UserNotFoundException(Integer id) {
		super("User with id: " + id + " not found. Please provide a valid user id. ");
	}
}
