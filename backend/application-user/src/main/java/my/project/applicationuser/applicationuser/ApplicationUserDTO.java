package my.project.applicationuser.applicationuser;

import java.util.List;

public record ApplicationUserDTO(
        Long id,
        String name,
        String surname,
        String email,
        String password,
        Gender gender,
        List<String> roles
) {
}