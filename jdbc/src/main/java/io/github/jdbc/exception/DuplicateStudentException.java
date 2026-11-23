package io.github.jdbc.exception;

public class DuplicateStudentException extends RuntimeException {
    public DuplicateStudentException(final String message, final Throwable cause) {
        super(message, cause);
    }
}