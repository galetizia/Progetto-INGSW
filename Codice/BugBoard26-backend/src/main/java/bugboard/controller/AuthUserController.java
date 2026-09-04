package bugboard.controller;

import bugboard.model.AuthUser;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try{
            String token = authUserService.login(request.email(), request.password());
            return ResponseEntity.ok(new LoginResponse(token));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout effettuato");
    }

    @PostMapping("/change_password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            authUserService.changePassword(request.email(), request.newPassword(), request.oldPassword());
            return ResponseEntity.ok("Password cambiata con successo");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
//un contenitore che mappa esattamente il JSON {"email": "...", "password": "..."}
record LoginRequest(String email, String password) {}
record LoginResponse(String token) {}
record ChangePasswordRequest(String email, String oldPassword, String newPassword) {}
