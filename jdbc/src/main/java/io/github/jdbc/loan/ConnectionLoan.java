package io.github.jdbc.loan;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.jdbc.properties.DatabaseProperties;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Singleton (Spring bean) that owns the connection pool and lends out connections.
 */
@Slf4j
@Component
public final class ConnectionLoan {

    private final HikariDataSource dataSource;

    public ConnectionLoan(final DatabaseProperties props) {
        final HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(props.url());
        cfg.setUsername(props.username());
        cfg.setPassword(props.password());
        cfg.setMaximumPoolSize(props.maxPoolSize());
        cfg.setMinimumIdle(props.minIdle());
        cfg.setConnectionTimeout(5_000);
        cfg.setPoolName("connection-loan-pool");
        this.dataSource = new HikariDataSource(cfg);
        log.info("Connection pool started: {}", props);
    }

    /**
     * Borrows a connection. close() (try-with-resources) returns it to the pool.
     */
    public Connection open() throws SQLException {
        return this.dataSource.getConnection();
    }

    /**
     * Runs work in ONE transaction on ONE connection: commit on success, rollback on failure.
     */
    public void inTransaction(final SqlWork work) throws SQLException {
        try (Connection c = open()) {
            c.setAutoCommit(false);
            try {
                work.run(c);
                c.commit();
            } catch (final SQLException | RuntimeException ex) {
                c.rollback();
                throw ex;
            }
        }
    }

    @PreDestroy
    void shutdown() {
        this.dataSource.close();
        log.info("Connection pool closed");
    }

    @FunctionalInterface
    public interface SqlWork {
        void run(Connection c) throws SQLException;
    }
}