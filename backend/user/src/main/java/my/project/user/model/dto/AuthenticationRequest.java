package my.project.user.model.dto;

public record AuthenticationRequest(
        String email,
        String password
) {
}
