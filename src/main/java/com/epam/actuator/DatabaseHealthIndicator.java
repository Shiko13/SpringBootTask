package com.epam.actuator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Override
    public Health health() {
        boolean databaseIsUp = checkDatabaseHealth();

        if (databaseIsUp) {
            return Health.up().build();
        } else {
            return Health.down().withDetail("error", "Database not reachable").build();
        }
    }

    private boolean checkDatabaseHealth() {
        try (Connection ignored = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword)) {
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
