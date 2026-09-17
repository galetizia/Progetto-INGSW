package bugboard.dto;

import bugboard.enums.Ruolo;

/**
 * Risposta restituita al client a seguito di un login riuscito, contenente il token JWT e le informazioni di base dell'utente.
 */
public record LoginResponse(
        String token,
        Ruolo ruoloUtente,
        int id) {}
