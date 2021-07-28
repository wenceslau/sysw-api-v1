package com.suite.app.exception;

public class FriendlyException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FriendlyException(String arg0) {

		super(arg0);

	}

	public FriendlyException(String arg0, Throwable cause) {

		super(arg0, cause);

	}

}
