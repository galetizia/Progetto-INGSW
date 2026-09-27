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

/**
 * Classe di test per {@link AuthUserService}.
 * Utilizza JUnit 5 e Mockito per testare la logica di business isolandola dal database.
 * Si concentra sulle operazioni di autenticazione, nello specifico il login degli utenti
 * e il cambio password, verificando sia i casi di successo che le eccezioni attese.
 * {@code @ExtendWith(MockitoExtension.class) serve per abilitare integrazione tra JUnit e Mockito}
 */
@ExtendWith(MockitoExtension.class)
class AuthUserServiceTest {

    /**
     * Mock del repository degli utenti.
     * Permette di simulare le operazioni sugli utenti senza accedere al DB reale.
     */
    @Mock
    private AuthUserRepository userRepository;

    /**
     * Mock del codificatore di password.
     * Usato per simulare controllo tra password in chiaro e quella crittografata.
     */
    @Mock
    private PasswordEncoder passwordEncoder;

    /**
     * Mock del servizio JWT.
     * Utilizzato per simulare la generazione del token di sicurezza al momento del login.
     */
    @Mock
    private JwtService jwtService;

    /**
     * L'istanza del servizio da testare.
     * L'annotazione {@code @InjectMocks} si occupa di creare l'oggetto e
     * iniettare automaticamente i mock definiti sopra al suo interno.
     */
    @InjectMocks
    private AuthUserService authUserService;

    /**
     * Testa il caso di successo del cambio password.
     * Verifica che:
     *      Non vengano lanciate eccezioni.
     *      La password dell'utente venga aggiornata con l'hash della nuova password.
     *      Il metodo di salvataggio del repository venga invocato esattamente una volta.
     */
    @Test
    @DisplayName("Password aggiornata")
    void testChangePassword_OK() {
        String email = "test1@bugboard.com";
        AuthUser user = new AuthUser();
        user.setEmail(email);
        user.setPassword("oldHashedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "oldHashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newHashedPassword");

        assertDoesNotThrow(() ->
            authUserService.changePassword(email, "oldPassword", "newPassword")
        );

        assertEquals("newHashedPassword", user.getPassword());

        verify(userRepository, times(1)).save(user);
    }

    /**
     * Testa il caso di fail nel cambio password quando l'email inserita non esiste.
     * Verifica che venga lanciata una {@link IllegalArgumentException} con il
     * messaggio di errore corretto e che l'operazione si interrompa.
     */
    @Test
    @DisplayName("FAIL: Utente non trovato")
    void testChangePassword_UtenteNonTrovato() {
        String email = "test2@bugboard.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () ->
                authUserService.changePassword(email, "oldPassword", "newPassword")
        );

        assertEquals("Email non valida", exc.getMessage());
    }

    /**
     * Testa il caso di fail nel cambio password quando la vecchia password inserita è errata.
     * Verifica che il controllo tramite {@link PasswordEncoder} fallisca e
     * restituisca una {@link IllegalArgumentException} specifica.
     */
    @Test
    @DisplayName("FAIl: vecchia password errata")
    void testChangePassword_OldPasswordErrata() {
        String email = "test3@bugboard.com";
        AuthUser user = new AuthUser();
        user.setEmail(email);
        user.setPassword("oldHashedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("passwordErrata", "oldHashedPassword")).thenReturn(false);

        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class, () ->
                authUserService.changePassword(email, "passwordErrata", "nuovaPassword"));

        assertEquals("Inserire la vecchia password corretta", exc.getMessage());
    }

    /**
     * Testa il caso di fail nel cambio password quando la nuova password
     * coincide esattamente con la vecchia password.
     * Verifica che il sistema impedisca di impostare una password identica alla precedente.
     */
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

    /**
     * Testa il caso di successo nell'operazione di login.
     * Verifica che fornendo credenziali corrette di un account attivo:
     *     Il token JWT generato non sia nullo.
     *     Il token restituito corrisponda esattamente a quello generato da {@link JwtService}.
     */
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

        assertNotNull(generatedToken);
        assertEquals("token", generatedToken);
    }

    /**
     * Testa il caso di fail nel login quando l'utente prova ad accedere
     * ma l'account risulta non è attivo.
     * Anche se le credenziali sono corrette, il sistema deve impedire l'accesso.
     */
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

    /**
     * Testa il caso di fail nel login quando la password inserita è errata.
     */
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

    /**
     * Testa il caso di fail nel login quando l'email fornita non
     * corrisponde a nessun account nel DB.
     */
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