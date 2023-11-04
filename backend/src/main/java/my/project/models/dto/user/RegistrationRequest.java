package my.project.models.dto.user;

public record RegistrationRequest(
        String name,
        String email,
        String password
) {
}
