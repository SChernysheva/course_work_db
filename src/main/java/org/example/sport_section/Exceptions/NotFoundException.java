package org.example.sport_section.Exceptions;

public class NotFoundException extends Exception {
    public String ex;
    public NotFoundException(String ex) {
        super(ex);
    }
}
