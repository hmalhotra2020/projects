package com.example.pumps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for Pumps Analyzer Application.
 * Combines Kafka producer (PurchaseSimulator), Kafka consumer (PurchaseAnalyzer),
 * and a live Chart.js web dashboard.
 */
@SpringBootApplication
@EnableScheduling
public class PumpsAnalyzerApp {
    public static void main(String[] args) {
        SpringApplication.run(PumpsAnalyzerApp.class, args);
    }
}
