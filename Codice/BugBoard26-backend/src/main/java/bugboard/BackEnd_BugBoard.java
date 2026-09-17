package bugboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale ed entry point dell'applicazione Spring Boot.
 * Avvia il server integrato e inizializza il contesto dell'applicazione backend.
 */
@SpringBootApplication
public class BackEnd_BugBoard {
    public static void main(String[] args) {
        SpringApplication.run(BackEnd_BugBoard.class, args);
    }
}
