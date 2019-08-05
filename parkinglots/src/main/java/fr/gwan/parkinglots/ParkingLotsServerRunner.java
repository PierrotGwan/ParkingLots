package fr.gwan.parkinglots;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

/**
 * Runner class for the parking lots library project component.
 */
@SpringBootApplication
@ComponentScan(basePackages = { "fr.gwan" })
public class ParkingLotsServerRunner {

    public static void main(String[] args) {
        SpringApplication.run(ParkingLotsServerRunner.class, args);
    }
}