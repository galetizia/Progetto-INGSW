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
import static org.mockito.Mockito.*;

//abilita integrazione JUnit e Mockito
@ExtendWith(MockitoExtension.class)
class AuthUserServiceTest {

    //@Mock crea un componente finto. Non accede al DB ma ci permette di deciderne il comportamento
    @Mock
    private AuthUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    //prende i mock e li inietta
    @InjectMocks
    private AuthUserService authUserService;


    @Test
    @DisplayName("Password aggiornata")
    void testChangePassword_OK() {
        String email = "test1@bugboard.com";
        AuthUser user = new AuthUser();
        user.setEmail(email);
        user.setPassword("oldHashedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        // istruisce il mock dicendo, quando ricevi x rispondi y
        when(passwordEncoder.matches("oldPassword", "oldHashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");

        // se lancia eccezione il test fallisce
        assertDoesNotThrow(() ->
            authUserService.changePassword(email, "oldPassword", "newPassword")
        );

        // verifica se il risultato attuale è uguale a quello atteso.
        assertEquals("newHashedPassword", user.getPassword());

        // verifica che il .save venga chiamato solo una volta
        verify(userRepository, times(1)).save(user);
    }


    @Test
    @DisplayName("FAIL: Utente non trovato")
    void testChangePassword_UtenteNonTrovato() {
        String email = "test2@bugboard.com";

        // simula quando il DB non trova nessun utente con quell'email
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // questa parte di codice si aspetta l'eccezione, se non si presenta fallisce
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () ->
                authUserService.changePassword(email, "oldPassword", "newPassword")
        );

        assertEquals("Email non valida", exc.getMessage());
    }


    @Test
    @DisplayName("FAIl: vecchia password errata")
    void testChangePassword_OldPasswordErrata() {
        String email = "test3@bugboard.com";
        AuthUser user = new AuthUser();
        user.setEmail(email);
        user.setPassword("oldHashedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // simula algoritmo delle password, rileva quando non combaciano
        when(passwordEncoder.matches("passwordErrata", "oldHashedPassword")).thenReturn(false);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () ->
                authUserService.changePassword(email, "passwordErrata", "nuovaPassword"));

        assertEquals("Inserire la vecchia password corretta", exc.getMessage());
    }


    @Test
    @DisplayName("FAIL: nuova = vecchia")
    void testChangePassword_StessaPassword() {
        String email = "test4@bugboard.com";
        AuthUser user = new AuthUser();
        user.setEmail(email);
        user.setPassword("oldHashedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("newPassword", "oldHashedPassword")).thenReturn(true);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () ->
                authUserService.changePassword(email, "newPassword", "newPassword"));

        assertEquals("La nuova password non può essere uguale alla precedente", exc.getMessage());
    }


    @Test
    @DisplayName("Login effettuato con successo")
    void testLogin_OK() {
        String email = "test5@bugboard.com";
        AuthUser user = new AuthUser();
        user.setEmail(email);
        user.setPassword("hashedPassword");

        user.setStatoAccount(true);
        user.setRuolo(Ruolo.INTERNAL_USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(user,"INTERNAL_USER")).thenReturn("token");

        String generatedToken = authUserService.login(email, "password");

        // verifica che la stringa non sia nulla
        assertNotNull(generatedToken);
        assertEquals("token", generatedToken);
    }


    @Test
    @DisplayName("FAIL: Account non attivo")
    void testLogin_AccountNonAttivo() {
        String email = "test6@bugboard.com";
        AuthUser user = new AuthUser();
        user.setEmail(email);
        user.setPassword("hashedPassword");

        user.setStatoAccount(false);
        user.setRuolo(Ruolo.INTERNAL_USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(true);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () ->
                authUserService.login(email, "password"));

        assertEquals("Account esistente ma non attivo", exc.getMessage());
    }


    @Test
    @DisplayName("FAIL: Password non valida")
    void testLogin_PasswordNonValida() {
        String email = "test7@bugboard.com";
        AuthUser user = new AuthUser();
        user.setEmail(email);
        user.setPassword("hashedPassword");

        user.setStatoAccount(true);
        user.setRuolo(Ruolo.INTERNAL_USER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(false);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () ->
                authUserService.login(email, "password"));

        assertEquals("Password non valida", exc.getMessage());
    }

    @Test
    @DisplayName("FAIL: Email non presente nel DB")
    void testLogin_EmailNonPresente() {
        String email = "test8@bugboard.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () ->
                authUserService.login(email, "password"));

        assertEquals("Email non valida", exc.getMessage());
    }
}