package bugboard.config;

import bugboard.enums.Ruolo;
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

            // --- 1. UTENTE ADMIN ---
            AuthUser admin = authUserRepository.findByEmail("admin@bugboard.com").orElse(new AuthUser());
            admin.setEmail("admin@bugboard.com");
            admin.setRuolo(Ruolo.ADMIN);
            admin.setPassword(passwordEncoder.encode("Password123!"));
            authUserRepository.save(admin);

            // --- 2. UTENTE ESTERNO ---
            AuthUser esterno = authUserRepository.findByEmail("external@bugboard.com").orElse(new AuthUser());
            esterno.setEmail("external@bugboard.com");
            esterno.setRuolo(Ruolo.EXTERNAL_USER);
            esterno.setPassword(passwordEncoder.encode("Password123!"));
            authUserRepository.save(esterno);

            // --- 3. UTENTE INTERNO ---
            AuthUser interno = authUserRepository.findByEmail("internal@bugboard.com").orElse(new AuthUser());
            interno.setEmail("internal@bugboard.com");
            interno.setRuolo(Ruolo.INTERNAL_USER);
            interno.setPassword(passwordEncoder.encode("Password123!"));
            authUserRepository.save(interno);

        };
    }
}