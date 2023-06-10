package my.project.application_user;

public record ApplicationUserRegistrationRequest(

        String name,
        String surname,
        String email
) {
}
