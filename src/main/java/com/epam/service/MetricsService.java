//package com.epam.service;
//
//import io.micrometer.core.instrument.Counter;
//import io.micrometer.core.instrument.MeterRegistry;
//import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
//import io.micrometer.prometheus.PrometheusConfig;
//import io.micrometer.prometheus.PrometheusMeterRegistry;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//public class MetricsService {
//
//    private final Counter loginCounter;
////    private final Counter registrationCounter;
//
//    public MetricsService() {
//        MeterRegistry meterRegistry = createMeterRegistry();
//        this.loginCounter = meterRegistry.counter("login_attempts", "Number of login attempts", "Second");
////        this.registrationCounter = meterRegistry.counter("registration_attempts", "Number of registration attempts");
//    }
//
//    private MeterRegistry createMeterRegistry() {
//        if (isPrometheusAvailable()) {
//            return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
//        } else {
//            return new SimpleMeterRegistry();
//        }
//    }
//
//    private boolean isPrometheusAvailable() {
//        try {
//            Class.forName("io.micrometer.prometheus.PrometheusMeterRegistry");
//            return true;
//        } catch (ClassNotFoundException e) {
//            return false;
//        }
//    }
//
//    public void incrementLoginCounter() {
//        loginCounter.increment();
//    }
//
////    public void incrementRegistrationCounter() {
////        registrationCounter.increment();
////    }
//}
