package bugboard.service;

import bugboard.model.AuthUser;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Gestisce la creazione e la configurazione dei token JWT necessari per l'autenticazione e l'autorizzazione all'interno dell'applicativo.
 */
@Service
public class JwtService {

    private static final long EXPIRATION_TIME_SECONDS = 3600;

    private final JwtEncoder jwtEncoder;

    /**
     * Inizializza il servizio passando il componente per la firma crittografica del token.
     *
     * @param jwtEncoder L'encoder incaricato di firmare il token con le chiavi RSA.
     */
    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    /**
     * Genera un token JWT che attesta l'identità dell'utente, configurando le relative tempistiche di validità e passando i ruoli come permessi (claims).
     *
     * @param user  L'oggetto che rappresenta l'utente autenticato.
     * @param ruolo Il ruolo associato all'utente, necessario a Spring Security per le autorizzazioni.
     * @return Il token JWT in formato stringa pronto da essere inviato al client.
     */
    public String generateToken(AuthUser user, String ruolo) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("bugboard")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(EXPIRATION_TIME_SECONDS))
                .subject(user.getEmail())
                .claim("role", ruolo)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
