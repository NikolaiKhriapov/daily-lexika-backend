package my.project.user.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import my.project.user.model.entity.Gender;

import java.time.LocalDate;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record UserDTO(
        Long id,
        String name,
        String surname,
        String email,
        String password,
        Gender gender,
        byte[] profilePhoto,
        List<String> roles,
        Long currentStreak,
        LocalDate dateOfLastStreak,
        Long recordStreak
) {
}