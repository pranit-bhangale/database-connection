package io.github.jdbc.repository;

import io.github.jdbc.entity.Student;
import io.github.jdbc.loan.ConnectionLoan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StudentRepository {

    private static final String INSERT =
            "INSERT INTO student (name, age, email, roll_no) VALUES (?, ?, ?, ?)";
    private static final String SELECT_BY_ID =
            "SELECT id, name, age, email, roll_no FROM student WHERE id = ?";
    private static final String SELECT_ALL =
            "SELECT id, name, age, email, roll_no FROM student ORDER BY id";
    private static final String UPDATE =
            "UPDATE student SET name = ?, age = ?, email = ?, roll_no = ? WHERE id = ?";
    private static final String DELETE =
            "DELETE FROM student WHERE id = ?";
    private static final String EXISTS =
            "SELECT 1 FROM student WHERE id = ?";

    private final ConnectionLoan connectionLoan;

    private static void bind(final PreparedStatement ps, final Student s) throws SQLException {
        ps.setString(1, s.getName());
        if (s.getAge() == null) ps.setNull(2, Types.INTEGER);
        else ps.setInt(2, s.getAge());
        ps.setString(3, s.getEmail());
        if (s.getRollNo() == null) ps.setNull(4, Types.INTEGER);
        else ps.setInt(4, s.getRollNo());
    }

    private static Student map(final ResultSet rs) throws SQLException {
        final int age = rs.getInt("age");
        final Integer ageValue = rs.wasNull() ? null : age;
        final int roll = rs.getInt("roll_no");
        final Integer rollValue = rs.wasNull() ? null : roll;
        return Student.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .age(ageValue)
                .email(rs.getString("email"))
                .rollNo(rollValue)
                .build();
    }

    public Student save(final Student student) throws SQLException {
        try (Connection c = connectionLoan.open();
             PreparedStatement ps = c.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, student);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) student.setId(keys.getLong("id"));
            }
            return student;
        }
    }

    public Optional<Student> findById(final Long id) throws SQLException {
        try (Connection c = connectionLoan.open();
             PreparedStatement ps = c.prepareStatement(SELECT_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public List<Student> findAll() throws SQLException {
        final List<Student> students = new ArrayList<>();
        try (Connection c = connectionLoan.open();
             PreparedStatement ps = c.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) students.add(map(rs));
        }
        return students;
    }

    /**
     * Returns true if a row was updated, false if no student has that id.
     */
    public boolean update(final Student student) throws SQLException {
        try (Connection c = connectionLoan.open();
             PreparedStatement ps = c.prepareStatement(UPDATE)) {
            bind(ps, student);
            ps.setLong(5, student.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Returns true if a row was deleted, false if no student has that id.
     */
    public boolean deleteById(final Long id) throws SQLException {
        try (Connection c = connectionLoan.open();
             PreparedStatement ps = c.prepareStatement(DELETE)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existsById(final Long id) throws SQLException {
        try (Connection c = connectionLoan.open();
             PreparedStatement ps = c.prepareStatement(EXISTS)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Batch insert: all rows in one transaction. All succeed or none do.
     */
    public void saveAll(final List<Student> students) throws SQLException {
        connectionLoan.inTransaction(c -> {
            try (PreparedStatement ps = c.prepareStatement(INSERT)) {
                for (final Student s : students) {
                    bind(ps, s);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        });
    }
}