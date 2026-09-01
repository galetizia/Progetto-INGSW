package bugboard.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import bugboard.service.ExternalUserService;

@RestController
@RequestMapping("/api/utente_esterno")
public class ExternalUserController {
    private final ExternalUserService externalUserService;

    public ExternalUserController(ExternalUserService externalUserService) {
        this.externalUserService = externalUserService;
    }
}
