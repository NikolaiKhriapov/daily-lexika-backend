package my.project.models.dto.user;

public record PasswordUpdateRequest(
        String passwordCurrent,
        String passwordNew
) {
}
