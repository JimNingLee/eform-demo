package com.cabsoft.pdf.form.utils;

@SuppressWarnings("serial")
public class IOUtilsException extends RuntimeException{

    public IOUtilsException(String message) {
        super(message);
    }

    public IOUtilsException(String message, Throwable cause) {
        super(message, cause);
    }
}
