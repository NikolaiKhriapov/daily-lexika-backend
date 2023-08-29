package my.project.user.auth;

public record AuthenticationRequest(
        String email,
        String password
) {
}
