package bugboard.dto;

/**
 * Payload utilizzato dagli amministratori per registrare e configurare un nuovo account nel sistema.
 */
public record CreateUserRequest(
        String email,
        String password,
        String ruolo) {}
