package bugboard.dto;

/**
 * Payload contenente i dati necessari per confermare e applicare la modifica della password di un utente.
 */
public record ChangePasswordRequest(
        String email,
        String oldPassword,
        String newPassword) {}
