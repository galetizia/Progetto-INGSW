package bugboard.service;

import bugboard.enums.Ruolo;
import bugboard.model.AuthUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import bugboard.repository.AuthUserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {
    @Mock
    private AuthUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthUserService authUserService;

    @Test
    @DisplayName("Issue assegnata con successo")
    void testAssegnaIssueUtente_Success(){

    }

    // Poi vanno fatti due ulteriori test, uno per quando l'issue non viene trovata, e uno per quando l'utente non viene trovato,
    // seguire il pattern di authUserServiceTest. Tutto questo sempre e solo per la funzionalità "AssegnaIssueUtente"


}