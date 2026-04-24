package com.cabsoft.config;

public class ConfigManagerException extends Exception {

	private static final long serialVersionUID = 2L;
	static final String CONFIG_NOT_FOUND = "Config was Not Found.";

    /**
     *
     */
    public ConfigManagerException() {
    	super();
    }

    /**
     *
     * @param message
     */
    public ConfigManagerException(String message) {
    	super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public ConfigManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param cause
     */
    public ConfigManagerException(Throwable cause) {
        super(cause);
    }
}
