package bugboard.dto;

public record AuthRequest(
        String email,
        String password) {}
