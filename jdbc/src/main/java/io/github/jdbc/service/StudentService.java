package io.github.jdbc.service;

import io.github.jdbc.dto.StudentRequest;
import io.github.jdbc.entity.Student;
import io.github.jdbc.exception.DuplicateStudentException;
import io.github.jdbc.exception.StudentNotFoundException;
import io.github.jdbc.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private static final String UNIQUE_VIOLATION = "23505";

    private final StudentRepository repository;

    private static Student toStudent(final Long id, final StudentRequest r) {
        return Student.builder().id(id).name(r.name()).age(r.age())
                .email(r.email()).rollNo(r.rollNo()).build();
    }

    private static RuntimeException translate(final SQLException ex) {
        if (UNIQUE_VIOLATION.equals(ex.getSQLState())) {
            return new DuplicateStudentException("Email or roll number already exists", ex);
        }
        return new IllegalStateException("Database error", ex);
    }

    public Student create(final StudentRequest r) {
        try {
            return repository.save(toStudent(null, r));
        } catch (final SQLException ex) {
            throw translate(ex);
        }
    }

    public Student get(final Long id) {
        try {
            return repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
        } catch (final SQLException ex) {
            throw translate(ex);
        }
    }

    public List<Student> list() {
        try {
            return repository.findAll();
        } catch (final SQLException ex) {
            throw translate(ex);
        }
    }

    public Student update(final Long id, final StudentRequest r) {
        try {
            final Student student = toStudent(id, r);
            if (!repository.update(student)) throw new StudentNotFoundException(id);
            return student;
        } catch (final SQLException ex) {
            throw translate(ex);
        }
    }

    public void delete(final Long id) {
        try {
            if (!repository.deleteById(id)) throw new StudentNotFoundException(id);
        } catch (final SQLException ex) {
            throw translate(ex);
        }
    }
}