package bugboard.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import bugboard.service.AuthUserService;

// Indica a Spring che questa classe riceve richieste web e risponde con dati
@RestController
// Definisce l'indirizzo base: tutti i metodi qui dentro inizieranno con "/api/utenti"
@RequestMapping("/api/utenti")
public class AuthUserController {
    private final AuthUserService authUserService;

    //Passiamo il service al controller
    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    // Gestisce le richieste POST in arrivo per registrare un nuovo utente
    @PostMapping("/registrazione")
    // @RequestBody converte automaticamente il JSON ricevuto in un oggetto AuthRequest
    public ResponseEntity<String> registrazione(@RequestBody AuthRequest request) {
        try{
            // Delega la logica al Service
            authUserService.registerAuthUser(request.email(), request.password());
            // Restituisce stato HTTP 200 (OK) se va tutto a buon fine
            return ResponseEntity.ok("Registrazione avvenuta con successo");
        } catch (IllegalArgumentException e){
            // Cattura gli errori (es. email duplicata) e restituisce HTTP 400 (Bad Request)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
//un contenitore che mappa esattamente il JSON {"email": "...", "password": "..."}
record AuthRequest(String email, String password) {}
