package com.epam.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Socket;

@Component
public class SocketHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        boolean isServiceReachable = checkCustomCondition();

        if (isServiceReachable) {
            return Health.up().withDetail("message", "Service is reachable").build();
        } else {
            return Health.down().withDetail("message", "Service is not reachable").build();
        }
    }

    private boolean checkCustomCondition() {
        try (Socket socket = new Socket()) {
            InetSocketAddress socketAddress = new InetSocketAddress("localhost", 8080);
            socket.connect(socketAddress, 1000);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
