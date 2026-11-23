package io.github.jdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class JdbcApplication {

    static void main(String[] args) {
        SpringApplication.run(JdbcApplication.class, args);
    }

}
