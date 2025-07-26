package my.project.dailylexika.user.service;

import my.project.dailylexika.user.model.entities.User;
import my.project.library.dailylexika.dtos.user.AccountDeletionRequest;
import my.project.library.dailylexika.dtos.user.PasswordUpdateRequest;
import my.project.library.dailylexika.dtos.user.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    void save(User user);
    boolean existsByEmail(String email);
    User getUserByEmail(String email);
    UserDto updateUserInfo(UserDto userDto);
    void updatePassword(PasswordUpdateRequest request);
    void deleteAccount(AccountDeletionRequest request);
    Page<UserDto> getPageOfUsers(Pageable pageable);
}
