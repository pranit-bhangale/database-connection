package io.github.jdbc.exception;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(final Long id) {
        super("Student not found: id=" + id);
    }
}