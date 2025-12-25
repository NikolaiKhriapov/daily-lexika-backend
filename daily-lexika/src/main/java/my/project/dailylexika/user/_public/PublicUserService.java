package my.project.dailylexika.user._public;

import my.project.dailylexika.user.model.entities.User;
import my.project.library.dailylexika.dtos.user.UserDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public interface PublicUserService {
    UserDto getUser();
    User getUserEntityByEmail(@NotBlank String email);
    void updateCurrentStreak(@NotNull @Min(0) Long newCurrentStreak);
    void updateRecordStreak(@NotNull @Min(0) Long newRecordStreak);
}
