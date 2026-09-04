package bugboard.service;

import bugboard.model.AuthUser;
import bugboard.repository.AdminRepository;
import bugboard.repository.AuthUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthUserService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AdminRepository adminRepository;

    public AuthUserService(AuthUserRepository authUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AdminRepository adminRepository) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.adminRepository = adminRepository;
    }

    public void registerAuthUser(String email, String password) {
        if (authUserRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email già in uso");
        }

        String hashedPass = passwordEncoder.encode(password);
        AuthUser newUser = new AuthUser(email, hashedPass);

        authUserRepository.save(newUser);
    }

    public String login (String email, String password) {

        AuthUser user = authUserRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Email/Password non valide"));

        if(!passwordEncoder.matches(password,user.getPassword())) {
            throw new IllegalArgumentException("Email/Password non valide");
        }

        String ruolo = adminRepository.existsById(user.getId()) ? "ADMIN" : "USER";

        return jwtService.generateToken(user, ruolo);
    }

    public void changePassword (String email, String oldPassword, String newPassword) {

        AuthUser user = authUserRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Email non valida"));

        if(!passwordEncoder.matches(oldPassword,user.getPassword())) {
            throw new IllegalArgumentException("Inserire la vecchia password corretta");
        }

        if(newPassword.equals(oldPassword)) {
            throw new IllegalArgumentException("La nuova password non può essere uguale alla precedente");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        authUserRepository.save(user);
    }
}
