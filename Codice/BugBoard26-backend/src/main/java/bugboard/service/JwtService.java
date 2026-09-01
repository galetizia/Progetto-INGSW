package bugboard.service;

import bugboard.model.AuthUser;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import bugboard.repository.AdminRepository;

import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final AdminRepository adminRepository;

    public JwtService(JwtEncoder jwtEncoder, AdminRepository adminRepository) {
        this.jwtEncoder = jwtEncoder;
        this.adminRepository = adminRepository;
    }

    public String generateToken(AuthUser user) {
        boolean isAdmin = adminRepository.existsById(user.getId());
        String ruolo = isAdmin ? "ADMIN" : "USER";
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("bugboard")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(user.getEmail())
                .claim("role", ruolo)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
