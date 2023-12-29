package my.project.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record UserDTO(
        @Nullable
        Long id,

        @Nullable
        String name,

        @Nullable
        String email,

        @Nullable
        String password,

        @Nullable
        List<String> roles,

        @Nullable
        Long currentStreak,

        @Nullable
        LocalDate dateOfLastStreak,

        @Nullable
        Long recordStreak
) {
}