package my.project.applicationuser.auth;

public record AuthenticationRequest(
        String email,
        String password
) {
}
