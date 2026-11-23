package io.github.jdbc.properties;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "database")
public record DatabaseProperties(
        String url,
        String username,
        String password,
        Integer maxPoolSize,
        Integer minIdle
) {
    public DatabaseProperties {
        maxPoolSize = maxPoolSize == null ? 10 : maxPoolSize;
        minIdle = minIdle == null ? 2 : minIdle;
    }

    @Override
    public @NonNull String toString() {
        return "DatabaseProperties[url=%s, username=%s]".formatted(url, username);
    }
}