package bugboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configurazione di Spring Security per la gestione degli accessi e delle autorizzazioni.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * L''algoritmo di hashing delle password.
     *
     * @return L'istanza di BCrypt utilizzata per cifrare e confrontare le password.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura la catena dei filtri HTTP, definendo rotte pubbliche e rotte protette,
     * e imposta la gestione delle sessioni in modalità stateless.
     *
     * @param http L'oggetto HttpSecurity da configurare.
     * @return La catena di filtri di sicurezza configurata.
     * @throws Exception Se si verifica un errore durante l'inizializzazione della configurazione.
     */
    @SuppressWarnings("squid:S4502")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/user/**").permitAll().
                        anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(_ -> {}));
        return http.build();
    }

    /**
     * Converte i claim del token JWT in autorizzazioni comprese da Spring Security,
     * aggiungendo automaticamente il prefisso "ROLE_".
     *
     * @return Il convertitore di autenticazione configurato.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("role");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}
