package org.example.userservice.exception;

public class ForbiddenException extends RuntimeException{

    public ForbiddenException(String message) {
        super(message);
    }

}
