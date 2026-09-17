package bugboard.dto;

public record CreateUserRequest(
        String email,
        String password,
        String ruolo) {}
