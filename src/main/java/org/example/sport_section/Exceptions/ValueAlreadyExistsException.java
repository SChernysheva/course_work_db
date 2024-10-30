package org.example.sport_section.Exceptions;

public class ValueAlreadyExistsException extends Exception {
    private String exception;
    public ValueAlreadyExistsException(String exception) {
        this.exception = exception;
    }
    public String getException() {
        return exception;
    }
}
