package bugboard.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import bugboard.service.AuthUserService;

// Indica a Spring che questa classe riceve richieste web e risponde con dati
@RestController
// Definisce l'indirizzo base: tutti i metodi qui dentro inizieranno con "/api/utenti"
@RequestMapping("/api/auth")
public class AuthUserController {
    private final AuthUserService authUserService;

    //Passiamo il service al controller
    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(String email, String password) {
        try{

        } catch (IllegalArgumentException e) {

        }
    }

}
//un contenitore che mappa esattamente il JSON {"email": "...", "password": "..."}
record AuthRequest(String email, String password) {}
