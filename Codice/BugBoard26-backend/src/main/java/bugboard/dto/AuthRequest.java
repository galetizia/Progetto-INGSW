package bugboard.dto;

/**
 * Payload contenente le credenziali inviate dal client per richiedere l'autenticazione.
 */
public record AuthRequest(
        String email,
        String password) {}
