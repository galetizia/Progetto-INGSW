package bugboard.dto;

import bugboard.enums.Ruolo;

public record LoginResponse(
        String token,
        Ruolo ruoloUtente,
        int id) {}
