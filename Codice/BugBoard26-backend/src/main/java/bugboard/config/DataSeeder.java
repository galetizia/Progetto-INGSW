package bugboard.config;

import bugboard.model.AuthUser;
import bugboard.repository.AuthUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner inizializzaUtenteTest(
            AuthUserRepository authUserRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            String emailTest = "admin@bugboard.com";

            if (authUserRepository.findByEmail(emailTest).isEmpty()) {

            AuthUser utent = new AuthUser();
            utent.setEmail(emailTest);

            utent.setPassword(passwordEncoder.encode("Password123!"));

            authUserRepository.save(utent);

            System.out.println("Utente creato con successo!");
            }
        };
    }
}