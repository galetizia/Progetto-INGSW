package bugboard.controller;

import bugboard.model.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import bugboard.service.AuthUserService;
import bugboard.dto.*;

import java.util.List;
import java.util.Map;

/**
 * Gestisce le richieste HTTP relative all'autenticazione, registrazione e gestione degli utenti.
 */
@RestController
@RequestMapping("/api/user")
public class AuthUserController {
    private final AuthUserService authUserService;

    /**
     * Inizializza il controller passando il service, necessario per le operazioni sugli utenti.
     *
     * @param authUserService Il service che contiene la logica di business per gli utenti.
     */
    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    /**
     * Autentica un utente verificandone le credenziali e genera un token JWT.
     *
     * @param request Oggetto DTO contenente l'email e la password fornite dall'utente.
     * @return Una risposta contenente il token JWT, il ruolo e l'id dell'utente se il login ha successo,
     *         oppure un errore 401 (Unauthorized) se le credenziali sono errate.
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody AuthRequest request) {
        try{
            String token = authUserService.login(request.email(), request.password());
            AuthUser utenteLoggato = authUserService.getUserByEmail(request.email());
            return ResponseEntity.ok(new LoginResponse(token, utenteLoggato.getRuolo(), utenteLoggato.getId()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Permette a un utente di aggiornare la propria password verificando prima quella attuale.
     *
     * @param request Oggetto DTO contenente l'email dell'utente, la vecchia password e la nuova password.
     * @return Una risposta di successo o un messaggio di errore (Bad Request) in caso di dati non validi.
     */
    @PostMapping("/cambia-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            authUserService.changePassword(request.email(), request.oldPassword(), request.newPassword());
            return ResponseEntity.ok("Password cambiata con successo");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * Crea un nuovo account utente nel sistema. Operazione riservata agli amministratori.
     *
     * @param request Oggetto DTO contenente l'email, la password temporanea e il ruolo da assegnare.
     * @return Messaggio di successo o errore.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/crea-utenti")
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {
        try {
            authUserService.registerAuthUser(request.email(), request.password(), request.ruolo());
            return ResponseEntity.ok("Utente registrato correttamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Abilita o disabilita l'account di un utente specifico. Operazione riservata agli amministratori.
     *
     * @param id L'identificativo dell'utente al quale modificare lo stato.
     * @return Messaggio di conferma del cambio di stato o errore se l'utente non viene trovato.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/cambia-stato")
    public ResponseEntity<String> changeState(@PathVariable int id) {
        try {
            authUserService.cambiaStatoUtente(id);
            return ResponseEntity.ok("Stato utente aggiornato");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Recupera tutte le issue attualmente assegnate a ciascun utente.
     *
     * @return Una mappa con l'email dell'utente come chiave e il conteggio delle issue assegnate.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/issues")
    public ResponseEntity<Map<String, Integer>> getIssuesPerUser() {
        return  ResponseEntity.ok(authUserService.getIssuesPerUser());
    }

    /**
     * Recupera tutte le issue che sono state risolte da ciascun utente.
     *
     * @return Una mappa con l'email dell'utente come chiave e il conteggio delle issue risolte.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/issue-risolte")
    public ResponseEntity<Map<String, Integer>> getRisoltePerUser() {
        return  ResponseEntity.ok(authUserService.getRisoltePerUser());
    }


    /**
     * Recupera il tempo medio impiegato da ciascun utente per risolvere le proprie issue.
     *
     * @return Una mappa con l'email dell'utente come chiave e il tempo medio di risoluzione (in ore).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tempo-per-user")
    public ResponseEntity<Map<String, Double>> getTimePerUser() {
        return ResponseEntity.ok(authUserService.getTimePerUser());
    }

    /**
     * Restituisce la lista completa di tutti gli utenti registrati nel database.
     *
     * @return Una lista di oggetti AuthUser.
     */
    @GetMapping("/elenco-utenti")
    public ResponseEntity<List<AuthUser>> getUsers() {
        return ResponseEntity.ok(authUserService.getAllUsers());
    }

}