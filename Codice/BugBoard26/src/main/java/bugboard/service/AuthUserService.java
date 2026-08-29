package bugboard.service;

import bugboard.model.AuthUser;
import bugboard.repository.AuthUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthUserService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthUserService(AuthUserRepository authUserRepository, PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerAuthUser(String email, String password) {
        if (authUserRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email già in uso");
        }

        String hashedPass = passwordEncoder.encode(password);
        AuthUser newUser = new AuthUser(email, hashedPass);

        authUserRepository.save(newUser);


    }
}
