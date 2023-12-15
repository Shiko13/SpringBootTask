package com.epam.actuator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
@Component
public class SocketHealthIndicator implements HealthIndicator {

    @Value("${server.hostname}")
    private String hostname;

    @Value("${server.port}")
    private Integer port;
    @Override
    public Health health() {
        boolean isServiceReachable = checkSocketHealth();

        if (isServiceReachable) {
            return Health.up().withDetail("message", "Service is reachable").build();
        }

        return Health.down().withDetail("message", "Service is not reachable").build();
    }

    private boolean checkSocketHealth() {
        try (Socket socket = new Socket()) {
            var socketAddress = new InetSocketAddress(hostname, port);
            socket.connect(socketAddress, 1000);

            return true;
        } catch (Exception e) {
            log.error("Error checking socket health", e);
            return false;
        }
    }
}
