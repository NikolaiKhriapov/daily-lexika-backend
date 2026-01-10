package my.project.library.admin.dtos.user;

import jakarta.validation.constraints.NotEmpty;
import my.project.library.admin.enumerations.RoleName;

public record UserDto(

        Integer id,

        @NotEmpty
        String name,

        @NotEmpty
        String email,

        RoleName role

) {
}
