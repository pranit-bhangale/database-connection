package io.github.jdbc.initializer;

import io.github.jdbc.loan.ConnectionLoan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Creates the table on startup. Use Flyway instead for real projects.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaInitializer implements ApplicationRunner {

    private final ConnectionLoan connectionLoan;

    @Override
    public void run(final @NonNull ApplicationArguments args) throws Exception {
        try (Connection c = connectionLoan.open(); Statement s = c.createStatement()) {
            s.execute("""
                    CREATE TABLE IF NOT EXISTS student (
                        id      BIGSERIAL PRIMARY KEY,
                        name    VARCHAR(100) NOT NULL,
                        age     INTEGER,
                        email   VARCHAR(255) NOT NULL UNIQUE,
                        roll_no INTEGER      NOT NULL UNIQUE
                    )
                    """);
            log.info("Schema ready");
        }
    }
}
