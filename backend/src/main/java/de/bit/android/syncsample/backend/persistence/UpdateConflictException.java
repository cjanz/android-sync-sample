package de.bit.android.syncsample.backend.persistence;

import javax.ejb.ApplicationException;

@ApplicationException(inherited = true, rollback = true)
public class UpdateConflictException extends RuntimeException {

	private static final long serialVersionUID = -3234488669666455938L;

	public UpdateConflictException() {
		super();
	}

	public UpdateConflictException(String message, Throwable cause) {
		super(message, cause);
	}

	public UpdateConflictException(String message) {
		super(message);
	}

	public UpdateConflictException(Throwable cause) {
		super(cause);
	}

}
