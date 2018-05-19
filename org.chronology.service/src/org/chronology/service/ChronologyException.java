package org.chronology.service;

public class ChronologyException extends RuntimeException {
	private static final long serialVersionUID = -1775471878658142814L;

	public ChronologyException() {
	}

	public ChronologyException(String message) {
		super(message);
	}

	public ChronologyException(Throwable cause) {
		super(cause);
	}

	public ChronologyException(String message, Throwable cause) {
		super(message, cause);
	}

	public ChronologyException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
