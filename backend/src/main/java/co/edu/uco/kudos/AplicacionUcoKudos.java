package co.edu.uco.kudos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AplicacionUcoKudos {

    public static void main(String[] args) {
        SpringApplication.run(AplicacionUcoKudos.class, args);
    }
}
