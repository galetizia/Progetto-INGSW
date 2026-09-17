package bugboard.dto;

public record ChangePasswordRequest(
        String email,
        String oldPassword,
        String newPassword) {}
