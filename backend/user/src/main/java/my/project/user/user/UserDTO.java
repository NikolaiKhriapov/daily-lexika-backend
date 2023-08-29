package my.project.user.user;

import java.util.List;

public record UserDTO(
        Long id,
        String name,
        String surname,
        String email,
        String password,
        Gender gender,
        byte[] profilePhoto,
        List<String> roles
) {
}