package com.mcnsa.chat.exceptions;

public class ChatSettingsException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5470440109687424801L;

	public ChatSettingsException(String message) {
		super(message);
	}

	public ChatSettingsException(String format, Object... args) {
		super(String.format(format, args));
	}

}
