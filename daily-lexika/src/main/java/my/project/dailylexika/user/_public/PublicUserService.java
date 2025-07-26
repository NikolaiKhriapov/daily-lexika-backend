package my.project.dailylexika.user._public;

import my.project.dailylexika.user.model.entities.User;
import my.project.library.dailylexika.dtos.user.UserDto;

public interface PublicUserService {
    UserDto getUser();
    User getUserEntityByEmail(String email);
    void updateCurrentStreak(Long newCurrentStreak);
    void updateRecordStreak(Long newRecordStreak);
}
