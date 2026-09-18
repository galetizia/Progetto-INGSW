package bugboard.config;

import bugboard.enums.Ruolo;
import bugboard.model.AuthUser;
import bugboard.repository.AuthUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configurazione per popolare il database con dati essenziali.
 * Viene eseguita da Spring Boot all'avvio dell'applicazione.
 */
@Configuration
public class DataSeeder {

    /**
     * Inserisce nel database gli utenti di default (Admin, External e Internal)
     * se non sono già presenti.
     *
     * @param authUserRepository La repository per gli utenti autenticati.
     * @param passwordEncoder    Il codificatore per cifrare le password prima di salvarla.
     * @return Il blocco di codice eseguito all'avvio da Spring.
     */
    @Bean
    public CommandLineRunner inizializzaUtenteTest(
            AuthUserRepository authUserRepository,
            PasswordEncoder passwordEncoder) {

        return _ -> {

            AuthUser admin = authUserRepository.findByEmail("admin@bugboard.com").orElse(new AuthUser());
            admin.setEmail("admin@bugboard.com");
            admin.setRuolo(Ruolo.ADMIN);
            admin.setPassword(passwordEncoder.encode("Password123!"));
            authUserRepository.save(admin);

            AuthUser esterno = authUserRepository.findByEmail("external@bugboard.com").orElse(new AuthUser());
            esterno.setEmail("external@bugboard.com");
            esterno.setRuolo(Ruolo.EXTERNAL_USER);
            esterno.setPassword(passwordEncoder.encode("Password123!"));
            authUserRepository.save(esterno);

            AuthUser interno = authUserRepository.findByEmail("internal@bugboard.com").orElse(new AuthUser());
            interno.setEmail("internal@bugboard.com");
            interno.setRuolo(Ruolo.INTERNAL_USER);
            interno.setPassword(passwordEncoder.encode("Password123!"));
            authUserRepository.save(interno);

        };
    }
}