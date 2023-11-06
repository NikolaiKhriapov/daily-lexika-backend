package my.project.models.dto.user;

public record AuthenticationRequest(
        String email,
        String password
) {
}
