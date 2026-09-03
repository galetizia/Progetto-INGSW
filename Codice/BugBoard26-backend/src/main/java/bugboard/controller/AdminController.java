package bugboard.controller;

import bugboard.service.AuthUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import bugboard.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final AuthUserService authUserService;
    public AdminController(AdminService adminService, AuthUserService authUserService) {
        this.adminService = adminService;
        this.authUserService = authUserService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/utenti")
    // @RequestBody converte automaticamente il JSON ricevuto in un oggetto RegisterRequest
    public ResponseEntity<String> createUser(@RequestBody RegisterRequest request) {
        try {
            // Delega la logica al Service
            authUserService.registerAuthUser(request.email(), request.password());
            // Restituisce stato HTTP 200 (OK) se va tutto a buon fine
            return ResponseEntity.ok("Utente registrato correttamente");
        } catch (IllegalArgumentException e) {
            // Cattura gli errori (es. email duplicata) e restituisce HTTP 400 (Bad Request)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
//un contenitore che mappa esattamente il JSON {"email": "...", "password": "..."}
record RegisterRequest(String email, String password) {}
