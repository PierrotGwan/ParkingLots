package fr.gwan.parkinglots;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Runner class for the parking lots library project component.
 */
@SpringBootApplication
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "fr.gwan" })
public class ParkingLotsServerRunner {

    public static void main(String[] args) {
        SpringApplication.run(ParkingLotsServerRunner.class, args);
    }
}