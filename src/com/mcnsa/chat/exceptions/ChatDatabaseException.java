package com.mcnsa.chat.exceptions;

public class ChatDatabaseException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7929858544878185984L;

	public ChatDatabaseException(String message) {
		super(message);
	}

	public ChatDatabaseException(String format, Object... args) {
		super(String.format(format, args));
	}
}
