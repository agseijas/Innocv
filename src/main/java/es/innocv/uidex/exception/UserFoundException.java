package es.innocv.uidex.exception;

public class UserFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UserFoundException(Integer id) {
		super("User with id: " + id + " is already in the system. ");
	}
}
