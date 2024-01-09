package my.project.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import my.project.models.entity.user.RoleName;

import java.util.Set;

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
        RoleName role,

        @Nullable
        Set<RoleStatisticsDTO> roleStatistics
) {
}
