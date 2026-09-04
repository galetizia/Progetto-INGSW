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

            AuthUser utente = authUserRepository.findByEmail(emailTest).orElseGet(() -> {
                AuthUser nuovoUtente = new AuthUser();
                nuovoUtente.setEmail(emailTest);
                return nuovoUtente;
            });

            utente.setPassword(passwordEncoder.encode("Password123!"));

            authUserRepository.save(utente);

        };
    }
}