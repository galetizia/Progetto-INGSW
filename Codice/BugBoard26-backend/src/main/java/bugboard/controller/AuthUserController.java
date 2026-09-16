package bugboard.controller;

import bugboard.enums.Ruolo;
import bugboard.model.AuthUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import bugboard.service.AuthUserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class AuthUserController {
    private final AuthUserService authUserService;

    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try{
            String token = authUserService.login(request.email(), request.password());
            AuthUser utenteLoggato = authUserService.getUserByEmail(request.email());
            return ResponseEntity.ok(new LoginResponse(token, utenteLoggato.getRuolo(), utenteLoggato.getId()));

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


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/crea_utenti")
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {
        try {
            authUserService.registerAuthUser(request.email(), request.password(), request.ruolo());
            return ResponseEntity.ok("Utente registrato correttamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/cambia_stato")
    public ResponseEntity<String> changeState(@PathVariable int id) {
        try {
            authUserService.cambiaStatoUtente(id);
            return ResponseEntity.ok("Stato utente aggiornato");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/issues")
    public ResponseEntity<Map<String, Integer>> getIssuesPerUser() {
        return  ResponseEntity.ok(authUserService.getIssuesPerUser());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/resolved_issues")
    public ResponseEntity<Map<String, Integer>> getRisoltePerUser() {
        return  ResponseEntity.ok(authUserService.getRisoltePerUser());
    }



    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/time_per_user")
    public ResponseEntity<Map<String, Double>> getTimePerUser() {
        return ResponseEntity.ok(authUserService.getTimePerUser());
    }

    @GetMapping("/elenco_utenti")
    public ResponseEntity<List<AuthUser>> getUsers() {
        return ResponseEntity.ok(authUserService.getAllUsers());
    }

}
record AuthRequest(String email, String password) {}
record LoginResponse(String token, Ruolo ruoloUtente, int id) {}
record ChangePasswordRequest(String email, String oldPassword, String newPassword) {}
record CreateUserRequest(String email, String password, String ruolo) {}